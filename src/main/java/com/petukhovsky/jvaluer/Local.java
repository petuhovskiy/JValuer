package com.petukhovsky.jvaluer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Arthur on 12/21/2015.
 */
public class Local {

    private static Logger logger = Logger.getLogger(Local.class.getName());

    private static Path runexe = null;

    public static Path loadResource(String name) {
        logger.fine("loading resource " + name);
        try {
            Path path = Files.createTempFile("", name.startsWith("/") ? name.substring(1) : name);
            path.toFile().deleteOnExit();
            try (InputStream is = Local.class.getResourceAsStream(name)) {
                Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
            }
            logger.fine("resource " + name + " was loaded");
            return path;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "resource loading failed", e);
        }
        return null;
    }

    public static Path getRunExe() {
        if (runexe == null) {
            if (isWindows()) runexe = loadResource("/runexe.exe");
            if (isOSX()) runexe = loadResource("/runexe_osx");
            if (isUnix()) runexe = loadResource("/runexe_linux");
            runexe.toFile().setExecutable(true);
            runexe.toFile().deleteOnExit();
        }
        return runexe;
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static boolean isUnix() {
        return !isOSX() && !isWindows();
    }

    public static boolean isOSX() {
        return System.getProperty("os.name").contains("OS X");
    }

    public static Process execute(String cmd) throws IOException {
        logger.fine("execute " + cmd);
        if (isWindows()) return Runtime.getRuntime().exec(cmd);
        else return Runtime.getRuntime().exec(new String[]{"bash", "-c", cmd});
    }

    public static Process execute(String[] cmd) throws IOException {
        logger.fine("execute " + Arrays.toString(cmd));
        if (isWindows()) return Runtime.getRuntime().exec(cmd);
        else {
            String[] args = new String[cmd.length + 2];
            args[0] = "bash";
            args[1] = "-c";
            System.arraycopy(cmd, 0, args, 2, cmd.length);
            return Runtime.getRuntime().exec(args);
        }
    }

    public static String getExecutableSuffix() {
        if (isWindows()) return ".exe";
        else return ".out";
    }
}
