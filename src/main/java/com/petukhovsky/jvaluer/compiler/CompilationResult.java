package com.petukhovsky.jvaluer.compiler;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Arthur on 12/25/2015.
 */
public class CompilationResult {

    private Path exe;
    private String comment;
    private boolean success;

    public CompilationResult(Path exe, String comment, boolean success) {
        this.exe = exe;
        this.comment = comment;
        this.success = success;
    }

    public boolean isSuccess() {
        return exe != null && Files.exists(exe) && success;
    }

    public Path getExe() {
        return exe;
    }

    public String getComment() {
        return comment;
    }

}
