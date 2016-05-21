package com.petukhovsky.jvaluer.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Created by petuh on 2/29/2016.
 */
public class CloneCompiler extends Compiler {
    @Override
    public CompilationResult compile(Path output, Path source, String... defines) {
        try {
            Files.copy(source, output, StandardCopyOption.REPLACE_EXISTING);
            return new CompilationResult(output, "", true);
        } catch (IOException e) {
            e.printStackTrace();
            return new CompilationResult(null, "Copy failed", false);
        }
    }
}
