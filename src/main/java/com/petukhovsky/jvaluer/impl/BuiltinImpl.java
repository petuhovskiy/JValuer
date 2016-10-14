package com.petukhovsky.jvaluer.impl;

import com.petukhovsky.jvaluer.commons.builtin.JValuerBuiltin;
import com.petukhovsky.jvaluer.commons.checker.Checker;
import com.petukhovsky.jvaluer.commons.checker.TokenChecker;
import com.petukhovsky.jvaluer.commons.gen.Generator;
import com.petukhovsky.jvaluer.commons.invoker.Invoker;

import java.util.Collections;
import java.util.Map;

/**
 * Created by arthur on 14.10.16.
 */
public class BuiltinImpl implements JValuerBuiltin {

    private final Map<String, Invoker> invokerMap;
    private final TokenChecker tokenChecker = new TokenChecker();

    public BuiltinImpl(Map<String, Invoker> invokerMap) {
        this.invokerMap = Collections.unmodifiableMap(invokerMap);
    }

    @Override
    public Checker checker(String s) {
        switch (s) {
            case "token":
                return tokenChecker;
            default:
                return null;
        }
    }

    @Override
    public Generator generator(String s) {
        return null;
    }

    @Override
    public Invoker invoker(String s) {
        return invokerMap.get(s);
    }
}
