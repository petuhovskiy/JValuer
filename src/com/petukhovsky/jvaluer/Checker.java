package com.petukhovsky.jvaluer;

import java.io.InputStream;

/**
 * Created by Arthur on 12/19/2015.
 */
public abstract class Checker {
    public CheckResult check(InputStream in, InputStream answer, InputStream out, Source source, RunInfo runInfo) {
        return check(in, answer, out);
    }

    public abstract CheckResult check(InputStream in, InputStream answer, InputStream out);
}
