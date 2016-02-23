package com.petukhovsky.jvaluer.compiler;

import com.petukhovsky.jvaluer.Local;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by petuh on 2/22/2016.
 */
public abstract class Compiler {
    public abstract CompilationResult compile(Path output, Path source, String... defines);

    public CompilationResult compile(Path source, String... defines) throws IOException {
        Path output = Files.createTempFile("", Local.getExecutableSuffix());
        output.toFile().deleteOnExit();
        return compile(output, source, defines);
    }
}
