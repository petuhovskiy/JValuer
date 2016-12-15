package com.petukhovsky.jvaluer.invoker;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.invoker.Invoker;
import com.petukhovsky.jvaluer.commons.run.RunInfo;
import com.petukhovsky.jvaluer.commons.run.RunOptions;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Arthur Petukhovsky on 6/13/2016.
 */
public class CustomInvoker implements Invoker {
    private final String pattern;
    private final Path executable;

    public CustomInvoker(String executable, String pattern) {
        this.pattern = pattern;
        this.executable = Paths.get(executable);
    }

    @Override
    public RunInfo run(JValuer jValuer, RunOptions options) {
        options = options.setArgs(processPattern(pattern, options.getArgs(), options.getExe().toString())).setExe(this.executable);
        return jValuer.invokeDefault(options);
    }

    private String processPattern(String pattern, String args, String executable) {
        return pattern.replace("{args}", args).replace("{exe}", "\"" + executable + "\"");
    }
}
