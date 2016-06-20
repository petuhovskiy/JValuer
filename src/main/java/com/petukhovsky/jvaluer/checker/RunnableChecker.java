package com.petukhovsky.jvaluer.checker;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.checker.CheckResult;
import com.petukhovsky.jvaluer.commons.checker.Checker;
import com.petukhovsky.jvaluer.commons.compiler.CompilationResult;
import com.petukhovsky.jvaluer.commons.data.StringData;
import com.petukhovsky.jvaluer.commons.data.TestData;
import com.petukhovsky.jvaluer.commons.run.RunInfo;
import com.petukhovsky.jvaluer.lang.Language;
import com.petukhovsky.jvaluer.run.Runner;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Arthur on 12/26/2015.
 */
public class RunnableChecker extends Checker implements Closeable, AutoCloseable {

    private Path exe;
    private Runner runner;

    private JValuer jValuer;

    private RunnableChecker(JValuer jValuer, Path source, Runner runner, Language language) {
        this.jValuer = jValuer;
        CompilationResult result = jValuer.compile(language, source);
        if (!result.isSuccess()) throw new RuntimeException("Can't compile checker:\n" + result.getComment());
        this.exe = result.getExe();
        this.runner = runner;
        this.runner.provideExecutable(exe);
    }

    public RunnableChecker(JValuer jValuer, Path source, Language language) {
        this(jValuer, source, jValuer.createRunner().setTimeLimit("10s").setMemoryLimit("512M").build(), language);
    }

    public RunnableChecker(JValuer jValuer, Path source) {
        this(jValuer, source, jValuer.getLanguages().findByPath(source));
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
