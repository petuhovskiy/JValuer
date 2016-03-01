package com.petukhovsky.jvaluer.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

/**
 * Created by petuh on 2/29/2016.
 */
public class CloneCompiler extends Compiler {
    @Override
    public CompilationResult compile(Path output, Path source, String... defines) {
        try {
            Files.copy(source, output, StandardCopyOption.REPLACE_EXISTING);
            return new CompilationResult(output, "All ok", true);
        } catch (IOException e) {
            e.printStackTrace();
            return new CompilationResult(null, "Copy failed", false);
        }
    }

    @Override
    public CompilationResult compile(Path source, String... defines) {
        Path output = null;
        try {
            output = Files.createTempFile("", "py");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't create temp file for compile output", e);
        }
        output.toFile().deleteOnExit();
        return compile(output, source, defines);
    }
}
