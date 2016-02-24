package com.petukhovsky.jvaluer.test;

/**
 * Created by petuh on 2/24/2016.
 */
public class Test {
    private TestData in;
    private TestData out;

    public Test(TestData in, TestData out) {
        this.in = in;
        this.out = out;
    }

    public TestData getIn() {
        return in;
    }

    public TestData getOut() {
        return out;
    }
}
