package com.petukhovsky.jvaluer.task;

import com.petukhovsky.jvaluer.checker.Checker;
import com.petukhovsky.jvaluer.checker.TokenChecker;
import com.petukhovsky.jvaluer.test.Test;

/**
 * Created by petuh on 2/24/2016.
 */
public class TestGroup {
    private Test[] tests;
    private Checker checker;
    private double cost;

    public TestGroup(Test[] tests, Checker checker, double cost) {
        this.tests = tests;
        this.checker = checker;
        this.cost = cost;
    }

    public TestGroup(Test[] tests) {
        this(tests, new TokenChecker(), 1D);
    }

    public TestGroup(Test[] tests, Checker checker) {
        this(tests, checker, 1D);
    }

    public double getMax() {
        return tests.length * cost;
    }


}
