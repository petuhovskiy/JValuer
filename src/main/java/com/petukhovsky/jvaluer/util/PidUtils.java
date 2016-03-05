package com.petukhovsky.jvaluer.util;

import java.lang.reflect.Field;

/**
 * Created by petuh on 3/5/2016.
 */
public class PidUtils {
    public static String getPid(Process process) {
        if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
            try {
                Field f = process.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                return "" + f.getInt(process);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
