package com.petukhovsky.jvaluer.compiler;

import com.petukhovsky.jvaluer.Local;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by petuh on 2/22/2016.
 */
public abstract class Compiler {

    private static Logger logger = Logger.getLogger(Compiler.class.getName());

    public abstract CompilationResult compile(Path output, Path source, String... defines);

    public CompilationResult compile(Path source, String... defines) {
        Path output = null;
        try {
            output = Files.createTempFile("", Local.getExecutableSuffix());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Can't create temp file for compile output", e);
        }
        output.toFile().deleteOnExit();
        return compile(output, source, defines);
    }
}
