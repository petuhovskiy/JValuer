package com.petukhovsky.jvaluer.lang;

import com.petukhovsky.jvaluer.commons.compiler.Compiler;
import com.petukhovsky.jvaluer.invoker.DefaultInvoker;
import com.petukhovsky.jvaluer.invoker.Invoker;

/**
 * Created by Arthur on 12/25/2015.
 */
public class Language {

    private final String name;
    private final Compiler compiler;
    private final Invoker invoker;

    public Language(String name, Compiler compiler, Invoker invoker) {
        this.name = name;
        this.compiler = compiler;
        this.invoker = invoker;
    }

    public Language(String name, Compiler compiler) {
        this(name, compiler, new DefaultInvoker());
    }

    public Compiler compiler() {
        return compiler;
    }

    public Invoker invoker() {
        return invoker;
    }

    public String name() {
        return name;
    }
}
