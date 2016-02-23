package com.petukhovsky.jvaluer;

import com.petukhovsky.jvaluer.compiler.Compiler;
import com.petukhovsky.jvaluer.compiler.RunnableCompiler;
import com.petukhovsky.jvaluer.invoker.DefaultInvoker;
import com.petukhovsky.jvaluer.invoker.Invoker;

import java.nio.file.Path;

/**
 * Created by Arthur on 12/25/2015.
 */
public enum Language {
    GNU_CPP("GNU C++", new RunnableCompiler("g++", "-static {defines} -lm -s -x c++ -Wl,--stack=268435456 -O2 -o {output} {source}")),
    GNU_CPP11("GNU C++11", new RunnableCompiler("g++", "-static {defines} -lm -s -x c++ -Wl,--stack=268435456 -O2 -std=c++11 -D__USE_MINGW_ANSI_STDIO=0 -o {output} {source}"));

    private String name;
    private Compiler compiler;
    private Invoker invoker;

    Language(String name, Compiler compiler, Invoker invoker) {
        this.name = name;
        this.compiler = compiler;
        this.invoker = invoker;
    }

    Language(String name, Compiler compiler) {
        this(name, compiler, new DefaultInvoker());
    }

    public static Language findByExtension(String ext) {
        switch (ext) {
            case "cpp":
                return GNU_CPP11;
            default:
                return null;
        }
    }

    public static Language findByPath(Path source) {
        String name = source.getFileName().toString();
        int pos = name.lastIndexOf('.');
        if (pos == -1) return null;
        return findByExtension(name.substring(pos + 1));
    }

    public static Language findByName(String name) {
        switch (name) {
            case "cpp":
                return GNU_CPP;
            case "c++":
                return GNU_CPP;
            case "сишка":
                return GNU_CPP;
            case "плюсы":
                return GNU_CPP;
            case "++":
                return GNU_CPP;
            case "11":
                return GNU_CPP11;
            case "c++11":
                return GNU_CPP11;
            case "cpp11":
                return GNU_CPP11;
            default:
                return null;
        }
    }

    public Compiler compiler() {
        return compiler;
    }

    public String getName() {
        return name;
    }
}
