package com.petukhovsky.jvaluer.run;

import com.petukhovsky.jvaluer.Local;
import com.petukhovsky.jvaluer.invoker.DefaultInvoker;
import com.petukhovsky.jvaluer.invoker.Invoker;
import com.petukhovsky.jvaluer.test.PathData;
import com.petukhovsky.jvaluer.test.TestData;

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

    private Path folder;
    private Path executable;
    private Path in, out;
    private String inFile, outFile;
    private Invoker invoker;
    private RunOptions options;

    Runner(Path folder, String in, String out, RunOptions options) {
        logger.fine("Creating runner with folder=" + folder + ", in=" + in + ", out=" + out + ", option=" + options);
        this.folder = folder;
        this.executable = folder.resolve("solution" + Local.getExecutableSuffix());

        this.in = folder.resolve(in);
        this.out = folder.resolve(out);

        this.inFile = in;
        this.outFile = out;

        this.invoker = new DefaultInvoker();

        logger.finer("Set executable file in runner = " + this.executable.toFile().setExecutable(true, false));
        logger.finer("Set readable file in runner = " + this.in.toFile().setReadable(true, false));
        logger.finer("Set writable file in runner = " + this.out.toFile().setWritable(true, false));

        this.options = options;
        this.updateOptions();
    }

    public Runner(String in, String out, RunOptions options) throws IOException {
        this(Files.createTempDirectory(""), in, out, options);
        this.folder.toFile().deleteOnExit();
        this.executable.toFile().deleteOnExit();
        this.in.toFile().deleteOnExit();
        this.out.toFile().deleteOnExit();
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

    public void clear() {
        try {
            clear(folder);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Can't clear runner folder", e);
        }
    }

    public void provideExecutable(Path path) {
        try {
            clear();
            Files.copy(path, executable);
            logger.finer("Set executable file in runner = " + this.executable.toFile().setExecutable(true, false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RunInfo run(String... args) {
        try {
            Files.createFile(out);
            logger.finer("Set readable file in runner = " + this.in.toFile().setReadable(true, false));
            logger.finer("Set writable file in runner = " + this.out.toFile().setWritable(true, false));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return invoker.run(args.length > 0 ? options.append("args", String.join(" ", args)) : options);
    }

    public RunInfo run(TestData testData, String... args) {
        return run(testData.openInputStream(), args);
    }

    public RunInfo run(InputStream test, String... args) {
        try {
            clear(folder, executable.getFileName().toString());
            Files.copy(test, in, StandardCopyOption.REPLACE_EXISTING);
            test.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return run(args);
    }

    public PathData getOutput() {
        return new PathData(out);
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    private void updateOptions() {
        this.options = this.options.append("executable", executable.toString())
                .append("folder", folder.toString());

        if (inFile.equals("stdin")) this.options = this.options.append("stdin", this.in.toString());
        if (outFile.equals("stdout")) this.options = this.options.append("stdout", this.out.toString());
        if (outFile.equals("stderr")) this.options = this.options.append("stderr", this.out.toString());
    }

    @Override
    public void close() throws IOException {
        clear();
        Files.deleteIfExists(folder);
    }
}
