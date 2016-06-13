package com.petukhovsky.jvaluer.invoker;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.run.RunInfo;
import com.petukhovsky.jvaluer.run.RunOptions;

/**
 * Created by Arthur Petukhovsky on 6/13/2016.
 */
public class CustomInvoker implements Invoker {
    private String pattern;
    private String executable;

    public CustomInvoker(String executable, String pattern) {
        this.pattern = pattern;
        this.executable = executable;
    }

    @Override
    public RunInfo run(JValuer jValuer, RunOptions options) {
        String args = options.optParameter("args", "");
        String executable = options.getParameter("executable");
        options = options.append("args", processPattern(pattern, args, executable))
                .append("executable", this.executable);
        return jValuer.invokeDefault(options);
    }

    private String processPattern(String pattern, String args, String executable) {
        return pattern.replaceAll("\\{args\\}", args)
                .replaceAll("\\{exe\\}", executable);
    }
}
