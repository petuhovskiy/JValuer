package com.petukhovsky.jvaluer.invoker;

import com.petukhovsky.jvaluer.RunInfo;
import com.petukhovsky.jvaluer.RunOptions;

/**
 * Created by Arthur on 12/18/2015.
 */
public interface Invoker {
    RunInfo run(RunOptions options);
}
