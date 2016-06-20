package com.petukhovsky.jvaluer.invoker;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.run.RunInfo;
import com.petukhovsky.jvaluer.commons.run.RunOptions;

/**
 * Created by Arthur on 12/18/2015.
 */
public interface Invoker {
    RunInfo run(JValuer jValuer, RunOptions options);
}
