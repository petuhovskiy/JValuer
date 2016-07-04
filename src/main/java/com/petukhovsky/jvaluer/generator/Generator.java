package com.petukhovsky.jvaluer.generator;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.data.GeneratedData;
import com.petukhovsky.jvaluer.commons.data.StringData;
import com.petukhovsky.jvaluer.commons.data.TestData;
import com.petukhovsky.jvaluer.commons.run.InvocationResult;
import com.petukhovsky.jvaluer.commons.run.RunLimits;
import com.petukhovsky.jvaluer.run.Runner;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Arthur on 12/25/2015.
 */
public class Generator implements Closeable, AutoCloseable {

    private static Logger log = Logger.getLogger(Generator.class.getName());

    private final Path exe;
    private final Runner runner;
    private final JValuer jValuer;

    public Generator(Path exe, JValuer jValuer) {
        this.jValuer = jValuer;
        this.exe = exe;
        this.runner = jValuer.createRunner().build(exe);
    }

    public GeneratedData generate(TestData in, RunLimits limits, String... args) {
        InvocationResult info = runner.run(in, limits, args);
        Path path = jValuer.createTempFile("gen", ".in");
        try (InputStream is = info.getOut().openInputStream()) {
            Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.log(Level.WARNING, "can't copy generated data", e);
        }
        return new GeneratedData(path, info.getRun());
    }

    public GeneratedData generate(RunLimits limits, String... args) {
        return generate(new StringData(""), limits, args);
    }

    @Override
    public void close() throws IOException {
        runner.close();
    }
}
