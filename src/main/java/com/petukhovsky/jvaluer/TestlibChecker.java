package com.petukhovsky.jvaluer;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Arthur on 12/26/2015.
 */
public class TestlibChecker extends Checker {

    private Path exe;
    private Runner runner;

    public TestlibChecker(Path source) throws IOException {
        CompilationResult result = Language.GNU_CPP.compile(source);
        if (!result.isSuccess()) throw new IOException("Can't compile checker:\n" + result.getComment());
        this.exe = result.getExe();
        this.runner = new Runner("stdin", "stderr", new RunOptions("trusted", ""));
        this.runner.provideExecutable(exe);
    }

    @Override
    public CheckResult check(TestData in, TestData answer, TestData out) {
        RunInfo info = runner.run(new StringData(""), String.format("\"%s\" \"%s\" \"%s\"", in.getPath(), out.getPath(), answer.getPath()));
        return new CheckResult(info.getExitCode() == 0, runner.getOutput().getString());
    }
}
