package com.petukhovsky.jvaluer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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

    public TestVerdict test(InputStream in, InputStream answer) {
        RunInfo info = runner.run(in);
        return estimator.estimate(in, answer, runner.getOutputStream(), info);
    }

    public TestVerdict test(String in, String answer) {
        RunInfo info = runner.run(in);
        return estimator.estimate(new ByteArrayInputStream(in.getBytes()), new ByteArrayInputStream(answer.getBytes()), runner.getOutputStream(), info);
    }

    public TestVerdict test(Path in, Path answer) {
        RunInfo info = runner.run(in);
        InputStream inStream, answerStream;
        inStream = answerStream = null;
        try {
            inStream = Files.newInputStream(in);
            answerStream = Files.newInputStream(answer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return estimator.estimate(inStream, answerStream, runner.getOutputStream(), info);
    }
}
