package com.petukhovsky.jvaluer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Arthur on 12/18/2015.
 */
public class Runner {

    Path folder, executable, in, out;
    Invoker invoker;
    RunOptions options;

    Runner(Path folder, String in, String out, Invoker invoker, RunOptions options) {
        this.folder = folder;
        this.folder.toFile().setExecutable(true, false);
        this.folder.toFile().setReadable(true, false);
        this.folder.toFile().setWritable(true, false);
        this.in = folder.resolve(in);
        this.out = folder.resolve(out);
        this.invoker = invoker;
        this.executable = folder.resolve("solution.exe");
        this.options = options
                            .append("executable", executable.toString())
                            .append("folder", folder.toString());
        if (in.equals("stdin")) options.append("stdin", folder.resolve("stdin").toString());
        if (out.equals("stdout")) options.append("stdout", folder.resolve("stdout").toString());
    }

    public Runner(String in, String out, Invoker invoker, RunOptions options) throws IOException {
        this(Files.createTempDirectory(""), in, out, invoker, options);
        this.folder.toFile().deleteOnExit();
    }

    private void clear(Path path, String... values) throws IOException {
        Set<String> list = Arrays.stream(values).distinct().collect(Collectors.toSet());
        Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!(file.getParent().equals(path) && list.contains(file.getFileName().toString()))) Files.delete(file);
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

    private RunInfo run() {
        try {
            Files.createFile(out);
            out.toFile().setWritable(true, false);
            in.toFile().setReadable(true, false);
            in.toFile().setExecutable(true, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return invoker.run(options);
    }

    public RunInfo run(Path input) {
        try {
            clear(folder, executable.getFileName().toString());
            Files.copy(input, in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return run();
    }

    public RunInfo run(String testData) {
        try {
            clear(folder, executable.getFileName().toString());
            Files.write(in, testData.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return run();
    }

    public RunInfo run(InputStream test) {
        try {
            clear(folder, executable.getFileName().toString());
            Files.copy(test, in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return run();
    }

    public Path getOutput() {
        return out;
    }

    public InputStream getOutputStream() {
        try {
            return Files.newInputStream(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getOutputText() throws IOException {
        return String.join("\n", Files.readAllLines(out));
    }
}
