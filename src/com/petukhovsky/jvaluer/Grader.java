package com.petukhovsky.jvaluer;

import java.nio.file.Path;

/**
 * Created by Arthur on 12/20/2015.
 */
public class Grader {

    private Runner runner;
    private Estimator estimator;

    public Grader(Runner runner, Estimator estimator) {
        this.runner = runner;
        this.estimator = estimator;
    }

    public void provideExecutable(Path exe) {
        runner.provideExecutable(exe);
    }

    public TestVerdict test(String in, String answer) {
        return test(new StringData(in), new StringData(answer));
    }

    public TestVerdict test(Path in, Path answer) {
        return test(new PathData(in), new PathData(answer));
    }

    public TestVerdict test(TestData in, TestData answer) {
        RunInfo info = runner.run(in);
        return estimator.estimate(in, answer, runner.getOutput(), info);
    }
}
