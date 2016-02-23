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

    private RunInfo() {
    }

    public static RunInfo crashed(String comment) {
        RunInfo info = new RunInfo();
        info.runVerdict = RunVerdict.CRASH;
        info.comment = comment;
        return info;
    }

    public static RunInfo completed(RunVerdict runVerdict, int exitCode, int userTime, int kernelTime, int passedTime, int consumedMemory, String comment) {
        RunInfo info = new RunInfo();
        info.runVerdict = runVerdict;
        info.exitCode = exitCode;
        if (exitCode != 0 && info.runVerdict == RunVerdict.SUCCESS) info.runVerdict = RunVerdict.RUNTIME_ERROR;
        info.userTime = userTime;
        info.kernelTime = kernelTime;
        info.passedTime = passedTime;
        info.consumedMemory = consumedMemory;
        info.comment = comment;
        return info;
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
