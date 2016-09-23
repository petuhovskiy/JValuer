package com.petukhovsky.jvaluer;

import com.petukhovsky.jvaluer.commons.compiler.CompilationResult;
import com.petukhovsky.jvaluer.commons.compiler.Compiler;
import com.petukhovsky.jvaluer.commons.local.Local;
import com.petukhovsky.jvaluer.commons.local.OSRelatedSupplier;
import com.petukhovsky.jvaluer.commons.local.OSRelatedValue;
import com.petukhovsky.jvaluer.commons.run.RunInfo;
import com.petukhovsky.jvaluer.commons.run.RunOptions;
import com.petukhovsky.jvaluer.generator.Generator;
import com.petukhovsky.jvaluer.invoker.Invoker;
import com.petukhovsky.jvaluer.invoker.NaiveInvoker;
import com.petukhovsky.jvaluer.invoker.RunexeInvoker;
import com.petukhovsky.jvaluer.lang.Language;
import com.petukhovsky.jvaluer.lang.Languages;
import com.petukhovsky.jvaluer.run.RunnerBuilder;
import com.petukhovsky.jvaluer.util.FilesUtils;
import org.apache.commons.io.FileUtils;

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
    public final String executableSuffix = new OSRelatedValue<String>().windows(".exe").orElse(".out");

    private Languages languages;

    private Path path;
    private Path temp;
    private Path resources;

    private Path runexe;

    private Invoker defaultInvoker;

    public JValuer(Languages languages, Path path) {
        this.languages = languages;
        this.path = path.toAbsolutePath();
        FilesUtils.assureEmptyDir(path);
        this.temp = path.resolve("temp");
        this.resources = path.resolve("resources");
        FilesUtils.assureEmptyDir(temp);
        FilesUtils.assureEmptyDir(resources);
        loadResources();
        loadInvoker();
    }

    private void loadInvoker() {
        defaultInvoker = new OSRelatedValue<Invoker>()
                .windows(new RunexeInvoker())
                .unix(new RunexeInvoker())
                .orElse(new NaiveInvoker());
    }

    private void loadResources() {
        this.runexe = new OSRelatedSupplier<Path>()
                .windows(() -> loadResource("runexe.exe", "/runexe.exe"))
                .unix(() -> loadResource("runexe", "/runexe_linux"))
                .orNull();
        if (this.runexe != null) Local.chmod777(this.runexe);

    }

    public Path loadResource(String name, String resource) {
        Path path = resources.resolve(name);
        Local.loadResource(path, resource);
        return path;
    }

    public void cleanTemp() {
        try {
            FileUtils.cleanDirectory(temp.toFile());
        } catch (IOException e) {
            logger.log(Level.WARNING, "can't clean temp", e);
        }
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
        Local.chmod777(exe);
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

    public CompilationResult compile(Path source, String... defines) {
        Language language = languages.findByPath(source);
        if (language == null) throw new RuntimeException("Can't determine language");
        return compile(language, source, defines);
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

    public RunInfo invokeDefault(RunOptions options) {
        return invoke(defaultInvoker, options);
    }

    public RunInfo invoke(Invoker invoker, RunOptions options) {
        return invoker.run(this, options);
    }
}
