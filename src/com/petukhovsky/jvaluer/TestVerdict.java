package com.petukhovsky.jvaluer;

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

    public boolean isAccepted() {
        return info.getRunVerdict() == RunVerdict.SUCCESS && check.isCorrect;
    }

    public String getVerdict() {
        return verdict;
    }

    public String getComment() {
        return check != null ? check.comment : "";
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
}
