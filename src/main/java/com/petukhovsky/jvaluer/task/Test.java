package com.petukhovsky.jvaluer.task;

import com.petukhovsky.jvaluer.test.TestData;

/**
 * Created by petuh on 2/24/2016.
 */
public class Test {
    private TestData in;
    private TestData out;

    private String testName = null;

    public Test(TestData in, TestData out) {
        this.in = in;
        this.out = out;
    }

    public Test(TestData in, TestData out, String testName) {
        this.in = in;
        this.out = out;
        this.testName = testName;
    }

    public TestData getIn() {
        return in;
    }

    public TestData getOut() {
        return out;
    }

    public String getTestName() {
        if (testName == null) return "";
        else return testName;
    }
}
