package com.petukhovsky.jvaluer.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by petuh on 3/2/2016.
 */
public class ArgumentParser {

    public static Map<String, String> parse(String[] args, int l, int r) {
        Map<String, String> map = new HashMap<>();
        for (int i = l; i < r; i++) {
            String arg = args[i];
            if (!arg.contains("=")) {
                map.put("", arg);
                continue;
            }
            int pos = arg.indexOf('=');
            String key = arg.substring(0, pos);
            String value = arg.substring(pos + 1);
            map.put(key, value);
        }
        return map;
    }

    public static Map<String, String> parse(String[] args) {
        return parse(args, 0, args.length);
    }

    public static Map<String, String> parse(Collection<String> args) {
        String[] arr = new String[args.size()];
        return parse(args.toArray(arr));
    }
}
