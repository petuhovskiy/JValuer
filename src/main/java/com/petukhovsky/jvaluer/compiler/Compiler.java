package com.petukhovsky.jvaluer.compiler;

import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Created by petuh on 2/22/2016.
 */
public abstract class Compiler {

    protected static Logger logger = Logger.getLogger(Compiler.class.getName());

    public abstract CompilationResult compile(Path output, Path source, String... defines);
}
