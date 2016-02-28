package com.petukhovsky.jvaluer.run;

import java.util.HashMap;

/**
 * Created by Arthur on 12/18/2015.
 */
public class RunOptions {
    private HashMap<String, String> params;

    public RunOptions() {
        params = new HashMap<>();
    }

    private RunOptions(HashMap<String, String> map) {
        this.params = map;
    }

    public RunOptions(String key, String value) {
        this();
        params.put(key, value);
    }

    public RunOptions append(String key, String value) {
        HashMap<String, String> map = new HashMap<>(params);
        map.put(key, value);
        return new RunOptions(map);
    }

    public boolean hasParameter(String key) {
        return params.containsKey(key);
    }

    public String getParameter(String key) {
        return params.get(key);
    }

    public String toString() {
        return params.toString();
    }
}
