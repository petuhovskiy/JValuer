package com.petukhovsky.jvaluer.run;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.invoker.DefaultInvoker;
import com.petukhovsky.jvaluer.invoker.Invoker;

import java.nio.file.Path;

/**
 * Created by Arthur Petukhovsky on 5/21/2016.
 */
public class RunnerBuilder {

    private Path dir;
    private JValuer jValuer;
    private RunOptions options;
    private Invoker invoker;

    private String in;
    private String out;

    public RunnerBuilder(Path dir, JValuer jValuer) {
        this.dir = dir;
        this.jValuer = jValuer;
        this.options = new RunOptions("folder", dir.toAbsolutePath().toString());
        this.invoker = new DefaultInvoker();
        this.in = "stdin";
        this.out = "stdout";
    }

    public RunnerBuilder addOption(String key, String value) {
        if (key.equals("folder") || key.equals("executable")) throw new RuntimeException("Forbidden key/value option");
        options = options.append(key, value);
        return this;
    }

    public RunnerBuilder setTimeLimit(String timeLimit) {
        return addOption("time_limit", timeLimit);
    }

    public RunnerBuilder setMemoryLimit(String memoryLimit) {
        return addOption("memory_limit", memoryLimit);
    }

    public RunnerBuilder setIn(String in) {
        this.in = in;
        return this;
    }

    public RunnerBuilder setOut(String out) {
        this.out = out;
        return this;
    }

    public RunnerBuilder setTrusted() {
        return addOption("trusted", "");
    }

    public RunnerBuilder setInvoker(Invoker invoker) {
        this.invoker = invoker;
        return this;
    }

    public Runner build() {
        return new Runner(jValuer, dir, options, invoker, in, out);
    }
}
