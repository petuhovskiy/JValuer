package com.petukhovsky.jvaluer.checker;

/**
 * Created by Arthur on 12/19/2015.
 */
public class CheckResult {
    private boolean isCorrect;
    private double result;
    private String comment;

    public CheckResult(boolean isCorrect, String comment) {
        this.isCorrect = isCorrect;
        this.comment = comment;
        this.result = isCorrect ? 1D : 0D;
    }

    public CheckResult(double result, String comment) {
        this.result = result;
        this.comment = comment;
        this.isCorrect = result == 1D;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public double getResult() {
        return result;
    }

    public String getComment() {
        return comment;
    }
}
