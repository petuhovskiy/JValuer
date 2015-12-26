package com.petukhovsky.jvaluer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Created by Arthur on 12/21/2015.
 */
public class Local {

    private static Path runexe = null;

    private static Invoker invoker = null;
    private static boolean debug = false;

    public static Path loadResource(String name) {
        try {
            Path path = Files.createTempFile("", name.startsWith("/") ? name.substring(1) : name);
            path.toFile().deleteOnExit();
            try (InputStream is = Local.class.getResourceAsStream(name)) {
                Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
            }
            return path;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Path getRunExe() {
        if (runexe == null) {
            if (isWindows()) runexe = loadResource("/runexe.exe");
            if (isOSX()) runexe = loadResource("/runexe_osx");
            if (isUnix()) runexe = loadResource("/runexe_linux");
            runexe.toFile().setExecutable(true);
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

    public static Invoker getInvoker() {
        if (invoker == null) {
            invoker = new DefaultInvoker();
        }
        return invoker;
    }

    public static Process execute(String cmd) throws IOException {
        if (isWindows()) return Runtime.getRuntime().exec(cmd);
        else return Runtime.getRuntime().exec(new String[]{"bash", "-c", cmd});
    }

    public static Process execute(String[] cmd) throws IOException {
        if (isWindows()) return Runtime.getRuntime().exec(cmd);
        else {
            String[] args = new String[cmd.length + 2];
            args[0] = "bash";
            args[1] = "-c";
            System.arraycopy(cmd, 0, args, 2, cmd.length);
            return Runtime.getRuntime().exec(args);
        }
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        Local.debug = debug;
    }

    public static String getExecutableSuffix() {
        if (isWindows()) return ".exe";
        else return ".out";
    }
}
