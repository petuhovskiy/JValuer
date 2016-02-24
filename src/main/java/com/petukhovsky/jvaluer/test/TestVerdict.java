package com.petukhovsky.jvaluer.test;

import com.petukhovsky.jvaluer.checker.CheckResult;
import com.petukhovsky.jvaluer.checker.Checker;
import com.petukhovsky.jvaluer.run.RunInfo;
import com.petukhovsky.jvaluer.run.RunVerdict;

/**
 * Created by Arthur on 12/20/2015.
 */
public class TestVerdict {

    private CheckResult check;
    private RunInfo info;
    private String verdict;

    public TestVerdict(CheckResult check, RunInfo info, String verdict) {
        this.check = check;
        this.info = info;
        this.verdict = verdict;
    }

    public TestVerdict(TestData in, TestData answer, TestData out, RunInfo info, Checker checker) {
        this.info = info;
        this.check = null;
        if (info.getRunVerdict() != RunVerdict.SUCCESS) {
            this.verdict = info.getRunVerdict().getText();
            return;
        }
        if (!out.exists()) {
            this.verdict = "Presentation Error";
            return;
        }
        this.check = checker.check(in, answer, out);
        this.verdict = this.check.isCorrect() ? "Accepted" : "Wrong answer";
    }

    public boolean isAccepted() {
        return info.getRunVerdict() == RunVerdict.SUCCESS && check != null && check.isCorrect();
    }

    public String getVerdict() {
        return verdict;
    }

    public String getComment() {
        return check != null ? check.getComment() : "";
    }

    public String getTime() {
        int time = info.getUserTime();
        return time + " ms";
    }

    public String getMemory() {
        int memory = info.getConsumedMemory();
        double kb = memory / 1024D;
        double mb = kb / 1024D;
        if (mb < 2D) return String.format("%.2fkb", kb);
        return String.format("%.2fMB", mb);
    }

    public String getRunComment() {
        return info.getComment();
    }

    @Override
    public String toString() {
        return "(" + getTime() + ", " + getMemory() + ") - " + verdict + ". " + getComment();
    }
}
