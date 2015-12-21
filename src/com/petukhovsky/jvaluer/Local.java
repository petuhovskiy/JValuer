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
    private static Invoker invoker = null;

    public static Path getWindowsRunExe() {
        if (windowsRunExe == null || !Files.exists(windowsRunExe))
            try {
                windowsRunExe = Files.createTempFile("", ".exe");
                windowsRunExe.toFile().deleteOnExit();
                Files.copy(WindowsInvoker.class.getResourceAsStream("/runexe.exe"), windowsRunExe, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return windowsRunExe;
    }

    public static Invoker getInvoker() {
        if (invoker == null) {
            //TODO return invoker based on OS
            invoker = new WindowsInvoker();
        }
        return invoker;
    }
}
