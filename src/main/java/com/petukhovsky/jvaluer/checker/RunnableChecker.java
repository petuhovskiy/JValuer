package com.petukhovsky.jvaluer.checker;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.checker.CheckResult;
import com.petukhovsky.jvaluer.commons.checker.Checker;
import com.petukhovsky.jvaluer.commons.compiler.CompilationResult;
import com.petukhovsky.jvaluer.commons.data.StringData;
import com.petukhovsky.jvaluer.commons.data.TestData;
import com.petukhovsky.jvaluer.commons.run.InvocationResult;
import com.petukhovsky.jvaluer.commons.run.RunLimits;
import com.petukhovsky.jvaluer.lang.Language;
import com.petukhovsky.jvaluer.run.Runner;

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

    public RunnableChecker(JValuer jValuer, Path source, Language language, RunLimits limits) {
        this.jValuer = jValuer;
        CompilationResult result = jValuer.compile(language, source);
        if (!result.isSuccess()) throw new RuntimeException("Can't compile checker:\n" + result.getComment());
        this.exe = result.getExe();
        this.runner = jValuer.createRunner().limits(limits).invoker(language.invoker()).build(exe);
    }

    public RunnableChecker(JValuer jValuer, Path source, Language language) {
        this(jValuer, source, language, new RunLimits(10000L, 1024L * 1024 * 512));
    }

    public RunnableChecker(JValuer jValuer, Path source) {
        this(jValuer, source, jValuer.getLanguages().findByPath(source));
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
