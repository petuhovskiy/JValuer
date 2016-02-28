package com.petukhovsky.jvaluer.value;

import com.petukhovsky.jvaluer.test.TestVerdict;

/**
 * Created by petuh on 2/28/2016.
 */
public class Value {

    private double factor;
    private double value;
    private double maxValue;
    private int acceptedTestsCount;
    private int testsCount;
    private Value[] values;
    private TestVerdict verdict;

    public Value(Value[] values, double factor) {
        this.factor = factor;
        value = maxValue = 0;
        testsCount = acceptedTestsCount = 0;
        this.values = values;
        for (Value value : values) {
            acceptedTestsCount += value.getAcceptedTestsCount();
            testsCount += value.getTestsCount();
            this.value += value.getValue() * this.factor;
            this.maxValue += value.getMaxValue() * this.factor;
        }
    }

    public Value(Value[] values) {
        this(values, 1D);
    }

    public Value(TestVerdict verdict, double factor) {
        this.factor = factor;
        this.verdict = verdict;
        maxValue = factor;
        testsCount = 1;
        if (verdict.isAccepted()) {
            acceptedTestsCount = 1;
            value = maxValue;
        } else {
            acceptedTestsCount = 0;
            value = 0;
        }
    }

    public Value(TestVerdict verdict) {
        this(verdict, 1);
    }

    public double getValue() {
        return value;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public int getAcceptedTestsCount() {
        return acceptedTestsCount;
    }

    public int getTestsCount() {
        return testsCount;
    }

    public boolean isAccepted() {
        return acceptedTestsCount == testsCount;
    }

    public TestVerdict[] getTestVerdicts() {
        if (verdict != null) return new TestVerdict[]{verdict};
        TestVerdict[] verdicts = new TestVerdict[testsCount];
        int it = 0;
        for (Value value : values) {
            TestVerdict[] tmp = value.getTestVerdicts();
            System.arraycopy(tmp, 0, verdicts, it, tmp.length);
            it += tmp.length;
        }
        return verdicts;
    }
}
