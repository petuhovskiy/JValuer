package com.petukhovsky.jvaluer.run;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.data.PathData;
import com.petukhovsky.jvaluer.commons.data.TestData;
import com.petukhovsky.jvaluer.commons.invoker.Invoker;
import com.petukhovsky.jvaluer.commons.local.Local;
import com.petukhovsky.jvaluer.commons.local.OSRelatedValue;
import com.petukhovsky.jvaluer.commons.run.InvocationResult;
import com.petukhovsky.jvaluer.commons.run.RunInOut;
import com.petukhovsky.jvaluer.commons.run.RunLimits;
import com.petukhovsky.jvaluer.commons.run.RunOptions;
import com.petukhovsky.jvaluer.util.FilesUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by arthur on 21.10.16.
 */
public class SafeRunner {

    private final static Logger logger = Logger.getLogger(SafeRunner.class.getName());

    private final JValuer jValuer;
    private final Path exe;
    private final RunOptions options;
    private final Invoker invoker;
    private final RunInOut inOut;

    SafeRunner(JValuer jValuer, Path exe, RunOptions options, Invoker invoker, RunInOut inOut) {
        this.jValuer = jValuer;
        this.exe = exe;
        this.options = options;
        this.invoker = invoker;
        this.inOut = inOut;
    }

    private Runner createRunner() {
        return new Runner(jValuer, jValuer.createTempDir(), exe, options, invoker, inOut);
    }

    private Path createTempOut() {
        return jValuer.createTempFile("saferun", ".out");
    }

    private InvocationResult migrate(InvocationResult result, Path dest) throws IOException {
        Files.copy(result.getOut().getPath(), dest, StandardCopyOption.REPLACE_EXISTING);
        return new InvocationResult(result.getRun(), new PathData(dest));
    }

    public InvocationResult run(TestData test, Path store, String... args) {
        try (Runner runner = createRunner()) {
            return migrate(runner.run(test, args), store);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "safe runner copy out", e);
        }
        return null;
    }

    public InvocationResult run(InputStream is, Path store, String... args) {
        try (Runner runner = createRunner()) {
            return migrate(runner.run(is, args), store);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "safe runner copy out", e);
        }
        return null;
    }

    public InvocationResult run(TestData test, String... args) {
        return run(test, createTempOut(), args);
    }

    public InvocationResult run(InputStream is, String... args) {
        return run(is, createTempOut(), args);
    }
}