package com.petukhovsky.jvaluer;

import com.petukhovsky.jvaluer.compiler.CompilationResult;
import com.petukhovsky.jvaluer.compiler.Compiler;
import com.petukhovsky.jvaluer.lang.Language;
import com.petukhovsky.jvaluer.lang.Languages;
import com.petukhovsky.jvaluer.run.RunnerBuilder;
import com.petukhovsky.jvaluer.test.Generator;
import com.petukhovsky.jvaluer.util.FilesUtils;
import com.petukhovsky.jvaluer.util.Local;
import com.petukhovsky.jvaluer.util.OSRelatedValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Arthur Petukhovsky on 5/21/2016.
 */
public class JValuer {

    private static final Logger logger = Logger.getLogger(JValuer.class.getName());
    public final String executableSuffix = new OSRelatedValue<String>().windows(".exe").value().orElse(".out");

    private Languages languages;

    private Path path;
    private Path temp;
    private Path resources;

    private Path runexe;

    public JValuer(Languages languages, Path path) {
        this.languages = languages;
        this.path = path;
        FilesUtils.clearFolder(path);
        this.temp = path.resolve("temp");
        this.resources = path.resolve("resources");
        try {
            Files.createDirectory(temp);
            Files.createDirectory(resources);
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadResources();
    }

    private void loadResources() {
        this.runexe = Local.loadResource("/runexe"
                + new OSRelatedValue<String>()
                .windows(".exe")
                .unix("_linux")
                .osx("_osx").value().get());
        Local.setExecutable(this.runexe);
    }

    public void clearTemp() {
        FilesUtils.clearFolder(temp);
    }

    public Path createTempFile(String prefix, String suffix, FileAttribute<?>... attrs) {
        try {
            return Files.createTempFile(temp, prefix, suffix, attrs);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "cannot create temp file", e);
        }
        return null;
    }

    public Path createTempExe() {
        Path exe = createTempFile("runnable", executableSuffix);
        Local.setExecutable(exe);
        return exe;
    }

    private Path createTempDir() {
        try {
            return Files.createTempDirectory(temp, "dir");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "cannot create temp directory", e);
        }
        return null;
    }

    public CompilationResult compile(Language language, Path source, String... defines) {
        return compile(language.compiler(), source, defines);
    }

    public CompilationResult compile(Compiler compiler, Path source, String... defines) {
        return compiler.compile(createTempExe(), source, defines);
    }

    public RunnerBuilder createRunner() {
        return new RunnerBuilder(createTempDir(), this);
    }

    public Languages getLanguages() {
        return languages;
    }

    public Path getRunexe() {
        return runexe;
    }

    public Generator createGenerator(Path exe) {
        return new Generator(exe, this);
    }
}
