package com.petukhovsky.jvaluer;

/**
 * Created by Arthur on 12/18/2015.
 */
public class RunInfo {
    private RunVerdict runVerdict;
    private int exitCode;
    private int userTime;
    private int kernelTime;
    private int passedTime;
    private int consumedMemory;
    private String comment;

    public void crashed(String comment) {
        runVerdict = RunVerdict.CRASHED;
        this.comment = comment;
    }

    public void completed(RunVerdict runVerdict, int exitCode, int userTime, int kernelTime, int passedTime, int consumedMemory, String comment) {
        this.runVerdict = runVerdict;
        this.exitCode = exitCode;
        if (exitCode != 0 && this.runVerdict == RunVerdict.SUCCESS) this.runVerdict = RunVerdict.RUNTIME_ERROR;
        this.userTime = userTime;
        this.kernelTime = kernelTime;
        this.passedTime = passedTime;
        this.consumedMemory = consumedMemory;
        this.comment = comment;
    }

    public RunVerdict getRunVerdict() {
        return runVerdict;
    }

    public int getExitCode() {
        return exitCode;
    }

    public int getUserTime() {
        return userTime;
    }

    public int getKernelTime() {
        return kernelTime;
    }

    public int getPassedTime() {
        return passedTime;
    }

    public int getConsumedMemory() {
        return consumedMemory;
    }

    public String getComment() {
        return comment;
    }
}
