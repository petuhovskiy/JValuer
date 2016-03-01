package com.petukhovsky.jvaluer.invoker;

import com.petukhovsky.jvaluer.run.RunInfo;
import com.petukhovsky.jvaluer.run.RunOptions;

/**
 * Created by petuh on 2/29/2016.
 */
public class PythonInvoker implements Invoker {

    private final String python;
    private final DefaultInvoker invoker;

    public PythonInvoker(String python) {
        this.python = python;
        this.invoker = new DefaultInvoker();
    }

    @Override
    public RunInfo run(RunOptions options) {
        String args = "";
        if (options.hasParameter("args")) args = options.getParameter("args");
        String executable = options.getParameter("executable");
        options = options.append("args", executable + " " + args)
                .append("executable", python);
        return invoker.run(options);
    }
}
