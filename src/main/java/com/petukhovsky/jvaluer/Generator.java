package com.petukhovsky.jvaluer;

import com.petukhovsky.jvaluer.test.GeneratedTest;
import com.petukhovsky.jvaluer.test.StringTest;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Arthur on 12/25/2015.
 */
public class Generator implements Closeable, AutoCloseable {

    private Path exe;
    private Runner runner;

    public Generator(Path exe) throws IOException {
        this.exe = exe;
        this.runner = new Runner("stdin", "stdout", new RunOptions("trusted", ""));
        this.runner.provideExecutable(exe);
    }

    public Generator(Path exe, String in, String out) throws IOException {
        this.exe = exe;
        this.runner = new Runner(in, out, new RunOptions("trusted", ""));
        this.runner.provideExecutable(exe);
    }

    public GeneratedTest generate(String... args) {
        RunInfo info = runner.run(new StringTest(""), args);
        return new GeneratedTest(runner.getOutput().getPath(), info);
    }

    @Override
    public void close() throws IOException {
        runner.close();
    }
}
