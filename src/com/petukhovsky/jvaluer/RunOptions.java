package com.petukhovsky.jvaluer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arthur on 12/18/2015.
 */
public class RunOptions {
    private Map<String, String> params;

    public RunOptions(){
        params = new HashMap<>();
    }

    public RunOptions(String key, String value){
        this();
        append(key, value);
    }

    public RunOptions append(String key, String value) {
        params.put(key, value);
        return this;
    }

    public boolean hasParameter(String key) {
        return params.containsKey(key);
    }

    public String getParameter(String key) {
        return params.get(key);
    }
}
