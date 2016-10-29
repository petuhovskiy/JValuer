package com.petukhovsky.jvaluer.checker;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.checker.CheckResult;
import com.petukhovsky.jvaluer.commons.checker.Checker;
import com.petukhovsky.jvaluer.commons.compiler.CompilationResult;
import com.petukhovsky.jvaluer.commons.data.StringData;
import com.petukhovsky.jvaluer.commons.data.TestData;
import com.petukhovsky.jvaluer.commons.lang.Language;
import com.petukhovsky.jvaluer.commons.run.InvocationResult;
import com.petukhovsky.jvaluer.commons.run.RunLimits;
import com.petukhovsky.jvaluer.commons.source.Source;
import com.petukhovsky.jvaluer.run.Runner;
import com.petukhovsky.jvaluer.run.RunnerBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Arthur on 12/26/2015.
 */
public class RunnableChecker extends Checker implements Closeable, AutoCloseable {

    private final Path exe;
    private final Runner runner;

    private final JValuer jValuer;

    public RunnableChecker(JValuer jValuer, Source source, RunLimits limits) {
        this.jValuer = jValuer;
        CompilationResult result = jValuer.compile(source);
        if (!result.isSuccess()) throw new RuntimeException("Can't compile checker:\n" + result.getComment());
        this.exe = result.getExe();
        this.runner = new RunnerBuilder(jValuer).limits(limits).build(exe, source);
    }

    public RunnableChecker(JValuer jValuer, Source source) {
        this(jValuer, source, RunLimits.of("10s", "256M"));
    }

    public RunnableChecker(JValuer jValuer, Path source) {
        this(jValuer, jValuer.getLanguages().autoSource(source));
    }

    @Override
    public CheckResult check(TestData in, TestData answer, TestData out) {
        InvocationResult result = runner.run(new StringData(""), in.getPath().toString(), out.getPath().toString(), answer.getPath().toString());
        return new CheckResult(result.getRun().getExitCode() == 0, result.getOut().getString());
    }

    @Override
    public void close() throws IOException {
        this.runner.close();
    }
}
