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
    private static Invoker invoker = null;

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
                Files.copy(Local.class.getResourceAsStream("/runexe"), linuxRunExe, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return linuxRunExe;
    }

    public static Invoker getInvoker() {
        if (invoker == null) {
            if (System.getProperty("os.name").toLowerCase().contains("win"))
                invoker = new WindowsInvoker();
            else invoker = new LinuxInvoker();
        }
        return invoker;
    }
}
