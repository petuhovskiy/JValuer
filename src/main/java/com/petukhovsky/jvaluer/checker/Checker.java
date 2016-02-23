package com.petukhovsky.jvaluer.checker;

import com.petukhovsky.jvaluer.RunInfo;
import com.petukhovsky.jvaluer.test.Test;

import java.nio.file.Path;

/**
 * Created by Arthur on 12/19/2015.
 */
public abstract class Checker {

    public CheckResult check(Test in, Test answer, Test out, Path source, RunInfo runInfo) {
        return check(in, answer, out);
    }

    public abstract CheckResult check(Test in, Test answer, Test out);
}
