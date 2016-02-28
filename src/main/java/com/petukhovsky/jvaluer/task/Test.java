package com.petukhovsky.jvaluer.task;

import com.petukhovsky.jvaluer.checker.Checker;
import com.petukhovsky.jvaluer.test.TestData;

/**
 * Created by petuh on 2/24/2016.
 */
public class Test {
    private TestData in;
    private TestData out;

    private String testName = null;

    private String memoryLimit = null;
    private String timeLimit = null;
    private Checker checker = null;

    public Test(TestData in, TestData out) {
        this.in = in;
        this.out = out;
    }

    public Test(TestData in, TestData out, String testName) {
        this.in = in;
        this.out = out;
        this.testName = testName;
    }

    public Test(TestData in, TestData out, String testName, Checker checker, String memoryLimit, String timeLimit) {
        this.in = in;
        this.out = out;
        this.testName = testName;
        this.checker = checker;
        this.memoryLimit = memoryLimit;
        this.timeLimit = timeLimit;
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
