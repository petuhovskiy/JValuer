package com.petukhovsky.jvaluer.test;

import com.petukhovsky.jvaluer.commons.checker.CheckResult;
import com.petukhovsky.jvaluer.commons.checker.Checker;
import com.petukhovsky.jvaluer.commons.data.TestData;
import com.petukhovsky.jvaluer.commons.run.RunInfo;
import com.petukhovsky.jvaluer.commons.run.RunVerdict;

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
        long time = info.getUserTime();
        return time + " ms";
    }

    public String getMemory() {
        return info.getMemoryString();
    }

    public String getRunComment() {
        return info.getComment();
    }

    @Override
    public String toString() {
        return "(" + getTime() + ", " + getMemory() + ") - " + verdict + ". " + getComment();
    }
}
