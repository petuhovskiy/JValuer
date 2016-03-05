package com.petukhovsky.jvaluer;

import com.petukhovsky.jvaluer.compiler.CloneCompiler;
import com.petukhovsky.jvaluer.compiler.Compiler;
import com.petukhovsky.jvaluer.compiler.RunnableCompiler;
import com.petukhovsky.jvaluer.invoker.DefaultInvoker;
import com.petukhovsky.jvaluer.invoker.Invoker;
import com.petukhovsky.jvaluer.invoker.PythonInvoker;

import java.nio.file.Path;

/**
 * Created by Arthur on 12/25/2015.
 */
public enum Language {
    GNU_CPP("GNU C++", new RunnableCompiler("g++", "-static {defines} -lm -s -x c++ -Wl,--stack=268435456 -O2 -o {output} {source}")),
    GNU_CPP11("GNU C++11", new RunnableCompiler("g++", Local.isOSX() ? " {defines} -lm -Wl,-stack_size -Wl,0x1000000 -O2 -D__USE_MINGW_ANSI_STDIO=0 -o {output} {source}" : "-static {defines} -lm -s -x c++ -Wl,--stack=268435456 -O2 -std=c++11 -D__USE_MINGW_ANSI_STDIO=0 -o {output} {source}")),
    PYTHON_3("Python 3", new CloneCompiler(), new PythonInvoker(Local.isWindows() ? "c:/Programs/Python-3/python.exe" : "python3"));

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
            case "py":
                return PYTHON_3;
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
            case "python":
                return PYTHON_3;
            case "python3":
                return PYTHON_3;
            case "py":
                return PYTHON_3;
            case "py3":
                return PYTHON_3;
            default:
                return null;
        }
    }

    public Compiler compiler() {
        return compiler;
    }

    public Invoker invoker() {
        return invoker;
    }

    public String getName() {
        return name;
    }
}
