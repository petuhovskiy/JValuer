package com.petukhovsky.jvaluer.local;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Arthur on 12/21/2015.
 */
public class Local {

    private static Logger logger = Logger.getLogger(Local.class.getName());

    public static Path loadResource(Path path, String name) {
        try {
            try (InputStream is = Local.class.getResourceAsStream(name)) {
                Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
            }
            logger.info("resource " + name + " was loaded");
            return path;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "resource " + name + " loading failed", e);
        }
        return null;
    }

    public static Process execute(String cmd) throws IOException {
        logger.fine("execute " + cmd);
        if (OS.isWindows()) return Runtime.getRuntime().exec(cmd);
        else return Runtime.getRuntime().exec(new String[]{"bash", "-c", cmd});
    }

    public static Process execute(String[] cmd) throws IOException {
        logger.fine("execute " + Arrays.toString(cmd));
        if (OS.isWindows()) return Runtime.getRuntime().exec(cmd);
        else {
            String[] args = new String[cmd.length + 2];
            args[0] = "bash";
            args[1] = "-c";
            System.arraycopy(cmd, 0, args, 2, cmd.length);
            return Runtime.getRuntime().exec(args);
        }
    }

    public static void chmod777(Path executable) {
        if (new OSRelatedValue<Boolean>().osx(true).unix(true).orElse(false)) {
            try {
                chmod777Unix(executable);
            } catch (IOException e) {
                logger.log(Level.WARNING, "posix permissions", e);
                chmod777Old(executable);
            }
        } else if (new OSRelatedValue<Boolean>().windows(true).orElse(false)) {
            chmod777Old(executable);
            /* TODO use this
            try {
                setExecutableWindows(executable);
            } catch (IOException e) {
                logger.log(Level.WARNING, "windows permissions", e);
                setExecutableOld(executable);
            }*/
        } else {
            chmod777Old(executable);
        }
    }

    public static void chmod777Old(Path executable) {
        File file = executable.toFile();
        file.setExecutable(true, false);
        file.setReadable(true, false);
        file.setWritable(true, false);
    }

    public static void chmod777Windows(Path executable) throws IOException {
        AclFileAttributeView aclAttr = Files.getFileAttributeView(executable, AclFileAttributeView.class);

        UserPrincipalLookupService upls = executable.getFileSystem().getUserPrincipalLookupService();
        UserPrincipal user = upls.lookupPrincipalByName(System.getProperty("user.name"));
        AclEntry.Builder builder = AclEntry.newBuilder();
        builder.setPermissions(EnumSet.of(AclEntryPermission.READ_DATA, AclEntryPermission.EXECUTE,
                AclEntryPermission.READ_ACL, AclEntryPermission.READ_ATTRIBUTES, AclEntryPermission.READ_NAMED_ATTRS,
                AclEntryPermission.WRITE_ACL, AclEntryPermission.DELETE
        ));
        builder.setPrincipal(user);
        builder.setType(AclEntryType.ALLOW);
        aclAttr.setAcl(Collections.singletonList(builder.build()));
    }

    public static void chmod777Unix(Path executable) throws IOException {
        Set<PosixFilePermission> perms = new HashSet<>();
        //add owners permission
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        //add group permissions
        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_WRITE);
        perms.add(PosixFilePermission.GROUP_EXECUTE);
        //add others permissions
        perms.add(PosixFilePermission.OTHERS_READ);
        perms.add(PosixFilePermission.OTHERS_WRITE);
        perms.add(PosixFilePermission.OTHERS_EXECUTE);
        Files.setPosixFilePermissions(executable, perms);
    }
}
