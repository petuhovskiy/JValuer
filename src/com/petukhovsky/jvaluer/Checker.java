package com.petukhovsky.jvaluer;

/**
 * Created by Arthur on 12/19/2015.
 */
public abstract class Checker {
    public CheckResult check(TestData in, TestData answer, TestData out, Source source, RunInfo runInfo) {
        return check(in, answer, out);
    }

    public abstract CheckResult check(TestData in, TestData answer, TestData out);
}
