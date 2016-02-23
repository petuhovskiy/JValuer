package com.petukhovsky.jvaluer.checker;

import com.petukhovsky.jvaluer.Language;
import com.petukhovsky.jvaluer.RunInfo;
import com.petukhovsky.jvaluer.RunOptions;
import com.petukhovsky.jvaluer.Runner;
import com.petukhovsky.jvaluer.compiler.CompilationResult;
import com.petukhovsky.jvaluer.test.StringTest;
import com.petukhovsky.jvaluer.test.Test;
import com.sun.istack.internal.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Arthur on 12/26/2015.
 */
public class TestlibChecker extends Checker implements Closeable, AutoCloseable {

    private Path exe;
    private Runner runner;

    public TestlibChecker(@NotNull Path source, @NotNull RunOptions options, @NotNull Language language) throws IOException {
        CompilationResult result = Language.GNU_CPP11.compiler().compile(source);
        if (!result.isSuccess()) throw new IOException("Can't compile checker:\n" + result.getComment());
        this.exe = result.getExe();
        this.runner = new Runner("stdin", "stderr", new RunOptions("trusted", ""));
        this.runner.provideExecutable(exe);
    }

    public TestlibChecker(Path source) throws IOException {
        this(source, new RunOptions("trusted", "").append("time_limit", "10s").append("memory_limit", "512M"), Language.detect(source));
    }

    @Override
    public CheckResult check(Test in, Test answer, Test out) {
        RunInfo info = runner.run(new StringTest(""), String.format("\"%s\" \"%s\" \"%s\"", in.getPath(), out.getPath(), answer.getPath()));
        return new CheckResult(info.getExitCode() == 0, runner.getOutput().getString());
    }

    @Override
    public void close() throws IOException {
        this.runner.close();
    }
}
