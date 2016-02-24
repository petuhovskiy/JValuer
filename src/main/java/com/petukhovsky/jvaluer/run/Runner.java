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
import java.util.stream.Collectors;

/**
 * Created by Arthur on 12/18/2015.
 */
public class Runner implements Closeable, AutoCloseable {

    private Path folder;
    private Path executable;
    private Path in, out;
    private Invoker invoker;
    private RunOptions options;

    Runner(Path folder, String in, String out, RunOptions options) {
        this.folder = folder;
        this.in = folder.resolve(in);
        this.out = folder.resolve(out);
        this.executable = folder.resolve("solution" + Local.getExecutableSuffix());
        this.invoker = new DefaultInvoker();

        this.executable.toFile().setExecutable(true, false);
        this.in.toFile().setReadable(true, false);
        this.out.toFile().setWritable(true, false);

        this.options = options
                .append("executable", executable.toString())
                .append("folder", folder.toString());

        if (in.equals("stdin")) this.options = this.options.append("stdin", this.in.toString());
        if (out.equals("stdout")) this.options = this.options.append("stdout", this.out.toString());
        if (out.equals("stderr")) this.options = this.options.append("stderr", this.out.toString());
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
            e.printStackTrace();
        }
    }

    public void provideExecutable(Path path) {
        try {
            clear();
            Files.copy(path, executable);
            executable.toFile().setExecutable(true, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RunInfo run(String... args) {
        try {
            Files.createFile(out);
            out.toFile().setWritable(true, false);
            in.toFile().setReadable(true, false);
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

    @Override
    public void close() throws IOException {
        clear();
        Files.deleteIfExists(folder);
    }
}
