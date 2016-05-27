package com.petukhovsky.jvaluer.local;

/**
 * Created by Arthur Petukhovsky on 5/21/2016.
 */
public class OS {

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static boolean isUnix() {
        return !isOSX() && !isWindows();
    }

    public static boolean isOSX() {
        return System.getProperty("os.name").contains("OS X");
    }

    public static boolean is64Bit() {
        if (System.getProperty("os.name").contains("Windows")) {
            return (System.getenv("ProgramFiles(x86)") != null);
        }
        else {
            return (System.getProperty("os.arch").contains("64"));
        }
    }
}
