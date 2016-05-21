package com.petukhovsky.jvaluer.invoker;

import com.petukhovsky.jvaluer.run.RunInfo;
import com.petukhovsky.jvaluer.run.RunOptions;

import java.util.logging.Logger;

/**
 * Created by Arthur on 12/18/2015.
 */
public class DefaultInvoker implements Invoker {

    private static Logger logger = Logger.getLogger(DefaultInvoker.class.getName());

    private static Invoker invoker;

    static {
        invoker = new RunexeInvoker();
    }

    @Override
    public RunInfo run(RunOptions options) {
        return invoker.run(options);
    }
}
