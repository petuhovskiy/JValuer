package com.petukhovsky.jvaluer.run;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.data.PathData;
import com.petukhovsky.jvaluer.commons.data.TestData;
import com.petukhovsky.jvaluer.commons.local.Local;
import com.petukhovsky.jvaluer.commons.run.InvocationResult;
import com.petukhovsky.jvaluer.commons.run.RunInOut;
import com.petukhovsky.jvaluer.commons.run.RunLimits;
import com.petukhovsky.jvaluer.commons.run.RunOptions;
import com.petukhovsky.jvaluer.commons.util.FilesUtils;
import com.petukhovsky.jvaluer.invoker.Invoker;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Arthur on 12/18/2015.
 */
public class Runner implements Closeable, AutoCloseable {

    private final static Logger logger = Logger.getLogger(Runner.class.getName());

    private final Path dir;
    private final Path executable;
    private final Path in, out;
    private final Invoker invoker;
    private final RunOptions options;

    private final JValuer jValuer;

    Runner(JValuer jValuer, Path dir, Path exe, RunOptions options, Invoker invoker, RunInOut inOut) {
        logger.fine("Creating runner with dir=" + dir + ", in=" + inOut.getIn() + ", out=" + inOut.getOut());
        this.dir = dir;
        this.executable = dir.resolve("solution" + jValuer.executableSuffix);
        Local.chmod777(this.executable);
        try {
            Files.copy(exe, executable, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("can't copy specified exe");
        }
        this.invoker = invoker;
        options = options.setExe(this.executable);

        this.jValuer = jValuer;

        this.in = dir.resolve(inOut.getIn());
        this.out = dir.resolve(inOut.getOut());

        if (inOut.getIn().equals("stdin")) options = options.setStdinForward(this.in);
        switch (inOut.getOut()) {
            case "stdout":
                options = options.setStdoutForward(this.out);
                break;
            case "stderr":
                options = options.setStderrForward(this.out);
                break;
        }
        this.options = options;
    }

    private void clear(Path path, String... values) throws IOException {
        Set<String> list = Arrays.stream(values).distinct().collect(Collectors.toSet());
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!(file.getParent().equals(path) && list.contains(file.getFileName().toString())))
                    Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (!dir.equals(path)) Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void clearButExe() {
        try {
            clear(dir, executable.getFileName().toString());
        } catch (IOException e) {
            logger.log(Level.WARNING, "can't clear dir", e);
        }
    }

    private InvocationResult run(RunOptions options) {
        try {
            Files.createFile(out);
            Local.chmod777(this.in);
            Local.chmod777(this.out);
        } catch (IOException e) {
            logger.log(Level.WARNING, "can't init in/out", e);
        }
        return new InvocationResult(jValuer.invoke(invoker, options), new PathData(out));
    }

    public InvocationResult run(TestData testData, String... args) {
        return run(testData.openInputStream(), null, args);
    }

    public InvocationResult run(TestData testData, RunLimits limits, String... args) {
        return run(testData.openInputStream(), limits, args);
    }

    public InvocationResult run(InputStream is, String... args) {
        return run(is, null, args);
    }

    public InvocationResult run(InputStream is, RunLimits limits, String... args) {
        RunOptions options = this.options;
        if (limits != null) options = options.setLimits(limits);
        if (args != null) options = options.setArgs(String.join(" ", args));
        return run(is, options);
    }

    private InvocationResult run(InputStream test, RunOptions options) {
        clearButExe();
        try (InputStream is = test) {
            Files.copy(is, in, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.log(Level.WARNING, "fail while copying", e);
        }
        return run(options);
    }

    @Override
    public void close() throws IOException {
        FilesUtils.deleteDirectory(dir);
    }
}
