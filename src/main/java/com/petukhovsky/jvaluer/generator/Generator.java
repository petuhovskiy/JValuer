package com.petukhovsky.jvaluer.generator;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.data.GeneratedData;
import com.petukhovsky.jvaluer.commons.data.StringData;
import com.petukhovsky.jvaluer.commons.run.RunInfo;
import com.petukhovsky.jvaluer.run.Runner;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Arthur on 12/25/2015.
 */
public class Generator implements Closeable, AutoCloseable {

    private Path exe;
    private Runner runner;

    public Generator(Path exe, JValuer jValuer) {
        this.exe = exe;
        this.runner = jValuer.createRunner().build();
        this.runner.provideExecutable(exe);
    }

    public GeneratedData generate(String... args) {
        RunInfo info = runner.run(new StringData(""), args);
        return new GeneratedData(runner.getOutput().getPath(), info);
    }

    @Override
    public void close() throws IOException {
        runner.close();
    }
}
