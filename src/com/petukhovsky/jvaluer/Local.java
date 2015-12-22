package com.petukhovsky.jvaluer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Created by Arthur on 12/21/2015.
 */
public class Local {

    private static Path windowsRunExe = null;
    private static Path linuxRunExe = null;
    private static Path OSXRunExe = null;

    private static Invoker invoker = null;
    private static boolean debug = false;

    public static Path getWindowsRunExe() {
        if (windowsRunExe == null || !Files.exists(windowsRunExe))
            try {
                windowsRunExe = Files.createTempFile("", ".exe");
                windowsRunExe.toFile().deleteOnExit();
                Files.copy(Local.class.getResourceAsStream("/runexe.exe"), windowsRunExe, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return windowsRunExe;
    }

    public static Path getLinuxRunExe(){
        if (linuxRunExe == null || !Files.exists(linuxRunExe))
            try {
                linuxRunExe = Files.createTempFile("", "");
                linuxRunExe.toFile().deleteOnExit();
                Files.copy(Local.class.getResourceAsStream("/runexe_linux"), linuxRunExe, StandardCopyOption.REPLACE_EXISTING);
                linuxRunExe.toFile().setExecutable(true, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return linuxRunExe;
    }

    public static Path getOSXRunExe() {
        if (OSXRunExe == null || !Files.exists(OSXRunExe))
            try {
                OSXRunExe = Files.createTempFile("", "");
                OSXRunExe.toFile().deleteOnExit();
                Files.copy(Local.class.getResourceAsStream("/runexe_osx"), OSXRunExe, StandardCopyOption.REPLACE_EXISTING);
                OSXRunExe.toFile().setExecutable(true, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return OSXRunExe;
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
            if (isWindows())
                invoker = new WindowsInvoker();
            if (isOSX())
                invoker = new OSXInvoker();
            if (isUnix())
                invoker = new LinuxInvoker();
        }
        return invoker;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        Local.debug = debug;
    }
}
