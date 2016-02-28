package com.petukhovsky.jvaluer.task;

import com.petukhovsky.jvaluer.checker.Checker;

/**
 * Created by petuh on 2/24/2016.
 */
public class TestGroup {
    private Test[] tests;
    private double factor;

    private String groupName;

    private Checker checker = null;
    private String timeLimit = null;
    private String memoryLimit = null;

    public TestGroup(Test[] tests, double factor) {
        this.tests = tests;
        this.factor = factor;
    }

    public TestGroup(Test[] tests) {
        this(tests, 1D);
    }

    public TestGroup(Test[] tests, double factor, String groupName, Checker checker, String timeLimit, String memoryLimit) {
        this.tests = tests;
        this.factor = factor;
        this.groupName = groupName;
        this.checker = checker;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
    }

    public String getGroupName() {
        return (groupName == null) ? "" : groupName;
    }

    public void appendTests(Test[] tests) {
        Test[] newTests = new Test[this.tests.length + tests.length];
        System.arraycopy(this.tests, 0, newTests, 0, this.tests.length);
        System.arraycopy(tests, 0, newTests, this.tests.length, tests.length);
        this.tests = newTests;
    }

    public void appendTest(Test test) {
        appendTests(new Test[]{test});
    }
}
