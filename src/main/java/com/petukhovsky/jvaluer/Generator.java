package com.petukhovsky.jvaluer;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Arthur on 12/25/2015.
 */
public class Generator {

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

    public GeneratedData generate(String... args) {
        RunInfo info = runner.run(new StringData(""), args);
        return new GeneratedData(runner.getOutput().getPath(), info);
    }
}
