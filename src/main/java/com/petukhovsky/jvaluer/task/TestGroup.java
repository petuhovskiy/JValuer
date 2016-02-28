package com.petukhovsky.jvaluer.task;

import com.petukhovsky.jvaluer.checker.Checker;

/**
 * Created by petuh on 2/24/2016.
 */
public class TestGroup {
    private Test[] tests;
    private double cost;
    private Checker checker = null;
    private String timeLimit = null;
    private String memoryLimit = null;

    public TestGroup(Test[] tests, double cost) {
        this.tests = tests;
        this.cost = cost;
    }

    public TestGroup(Test[] tests) {
        this(tests, 1D);
    }

    public TestGroup(Test[] tests, double cost, Checker checker, String timeLimit, String memoryLimit) {
        this.tests = tests;
        this.cost = cost;
        this.checker = checker;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
    }

    public double getMax() {
        return tests.length * cost;
    }

}
