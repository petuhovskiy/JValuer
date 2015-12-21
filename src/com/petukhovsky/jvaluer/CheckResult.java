package com.petukhovsky.jvaluer;

/**
 * Created by Arthur on 12/19/2015.
 */
public class CheckResult {
    boolean isCorrect;
    double result;
    String comment;

    public CheckResult(boolean isCorrect, String comment) {
        this.isCorrect = isCorrect;
        this.comment = comment;
        this.result = isCorrect ? 1D : 0D;
    }

    public CheckResult(double result, String comment) {
        this.result = result;
        this.comment = comment;
        this.isCorrect = result != 0D;
    }
}
