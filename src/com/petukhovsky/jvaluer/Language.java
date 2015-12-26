package com.petukhovsky.jvaluer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Arthur on 12/25/2015.
 */
public enum Language {
    GNU_CPP("GNU C++", "g++", "-static {defines} -lm -s -x c++ -Wl,--stack=268435456 -O2 -o {output} {source}"),
    GNU_CPP11("GNU C++11", "g++", "-static {defines} -lm -s -x c++ -Wl,--stack=268435456 -O2 -std=c++11 -D__USE_MINGW_ANSI_STDIO=0 -o {output} {source}");

    private String name;
    private String compiler;
    private String compilePattern;

    Language(String name, String compiler, String compilePattern) {
        this.name = name;
        this.compiler = compiler;
        this.compilePattern = compilePattern;
    }

    public static Language findByExtension(String ext) {
        switch (ext) {
            case "cpp":
                return GNU_CPP;
            default:
                return null;
        }
    }

    public CompilationResult compile(Path output, Path source, String... defines) {
        try {
            Files.deleteIfExists(output);
        } catch (IOException e) {
            e.printStackTrace();
            return new CompilationResult(output, "Access denied", false);
        }
        String cmd = compiler + " "
                + compilePattern.replace("{defines}", defines.length > 0 ? "-D " + String.join(" -D") : "")
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
            e.printStackTrace();
            return new CompilationResult(output, "Something went wrong", false);
        }
    }

    public CompilationResult compile(Path source, String... defines) throws IOException {
        Path output = Files.createTempFile("", Local.getExecutableSuffix());
        output.toFile().deleteOnExit();
        return compile(output, source, defines);
    }
}
