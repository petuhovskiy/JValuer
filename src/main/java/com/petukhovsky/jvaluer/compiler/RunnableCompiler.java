package com.petukhovsky.jvaluer.compiler;

import com.petukhovsky.jvaluer.Local;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by petuh on 2/23/2016.
 */
public class RunnableCompiler extends Compiler {

    private static Logger logger = Logger.getLogger(RunnableCompiler.class.getName());

    private String exe;
    private String pattern;

    public RunnableCompiler(String exe, String pattern) {
        this.exe = exe;
        this.pattern = pattern;
    }

    @Override
    public CompilationResult compile(Path output, Path source, String... defines) {
        try {
            Files.deleteIfExists(output);
        } catch (IOException e) {
            e.printStackTrace();
            return new CompilationResult(output, "Access denied", false);
        }
        String cmd = exe + " "
                + pattern.replace("{defines}", defines.length > 0 ? "-D " + String.join(" -D") : "")
                .replace("{output}", "\"" + output.toString() + "\"")
                .replace("{source}", "\"" + source.toString() + "\"");
        try {
            Process process = Local.execute(cmd);
            process.waitFor(60, TimeUnit.SECONDS);
            if (process.isAlive()) {
                process.destroyForcibly();
                return new CompilationResult(output, "Time limit exceeded", false);
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String comment = bufferedReader.lines().collect(Collectors.joining("\n"));
            bufferedReader.close();
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            comment += bufferedReader.lines().collect(Collectors.joining("\n"));
            bufferedReader.close();
            return new CompilationResult(output, comment, true);
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Compilation failed", e);
            return new CompilationResult(output, "Something went wrong", false);
        }
    }
}
