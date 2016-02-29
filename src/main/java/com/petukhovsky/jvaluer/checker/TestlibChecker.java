package com.petukhovsky.jvaluer.checker;

import com.petukhovsky.jvaluer.Language;
import com.petukhovsky.jvaluer.compiler.CompilationResult;
import com.petukhovsky.jvaluer.run.RunInfo;
import com.petukhovsky.jvaluer.run.RunOptions;
import com.petukhovsky.jvaluer.run.Runner;
import com.petukhovsky.jvaluer.test.StringData;
import com.petukhovsky.jvaluer.test.TestData;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Arthur on 12/26/2015.
 */
public class TestlibChecker extends Checker implements Closeable, AutoCloseable {

    private Path exe;
    private Runner runner;

    public TestlibChecker(Path source, RunOptions options, Language language) throws IOException {
        CompilationResult result = Language.GNU_CPP11.compiler().compile(source);
        if (!result.isSuccess()) throw new IOException("Can't compile checker:\n" + result.getComment());
        this.exe = result.getExe();
        this.runner = new Runner();
        this.runner.provideExecutable(exe);
    }

    public TestlibChecker(Path source) throws IOException {
        this(source, new RunOptions("trusted", "").append("time_limit", "10s").append("memory_limit", "512M"), Language.findByPath(source));
    }

    @Override
    public CheckResult check(TestData in, TestData answer, TestData out) {
        RunInfo info = runner.run(new StringData(""), String.format("\"%s\" \"%s\" \"%s\"", in.getPath(), out.getPath(), answer.getPath()));
        return new CheckResult(info.getExitCode() == 0, runner.getOutput().getString());
    }

    @Override
    public void close() throws IOException {
        this.runner.close();
    }
}
