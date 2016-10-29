package com.petukhovsky.jvaluer.impl;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.builtin.JValuerBuiltin;
import com.petukhovsky.jvaluer.commons.compiler.CompilationResult;
import com.petukhovsky.jvaluer.commons.compiler.Compiler;
import com.petukhovsky.jvaluer.commons.invoker.Invoker;
import com.petukhovsky.jvaluer.commons.lang.Language;
import com.petukhovsky.jvaluer.commons.lang.Languages;
import com.petukhovsky.jvaluer.commons.local.Local;
import com.petukhovsky.jvaluer.commons.local.OSRelatedSupplier;
import com.petukhovsky.jvaluer.commons.local.OSRelatedValue;
import com.petukhovsky.jvaluer.commons.local.UserAccount;
import com.petukhovsky.jvaluer.commons.run.RunInfo;
import com.petukhovsky.jvaluer.commons.run.RunOptions;
import com.petukhovsky.jvaluer.commons.source.Source;
import com.petukhovsky.jvaluer.invoker.NaiveInvoker;
import com.petukhovsky.jvaluer.invoker.RunexeInvoker;
import com.petukhovsky.jvaluer.util.FilesUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by arthur on 14.10.16.
 */
public class JValuerImpl implements JValuer {

    private static final Logger logger = Logger.getLogger(JValuer.class.getName());

    private final Languages languages;

    private final Path path;
    private final UserAccount defaultAccount;
    private final Path temp;
    private final Path resources;

    private final JValuerBuiltin builtin;

    public JValuerImpl(Languages languages, Path path, UserAccount defaultAccount) {
        this.languages = languages;
        this.path = path;
        this.defaultAccount = defaultAccount;
        FilesUtils.assureEmptyDir(path);
        this.temp = path.resolve("temp");
        this.resources = path.resolve("resources");
        FilesUtils.assureEmptyDir(this.temp);
        FilesUtils.assureEmptyDir(this.resources);

        Map<String, Invoker> invokerMap = new HashMap<>();

        Invoker naive = new NaiveInvoker();

        invokerMap.put("naive", naive);

        Path runexe = new OSRelatedSupplier<Path>()
                .windows(() -> loadResource("runexe.exe", "/runexe.exe"))
                .unix(() -> loadResource("runexe", "/runexe_linux"))
                .orElse(() -> null);
        FilesUtils.chmod(runexe, 111);
        if (runexe != null) {
            Invoker invoker = new RunexeInvoker(runexe);
            invokerMap.put("default", invoker);
            invokerMap.put("runexe", invoker);
        } else {
            invokerMap.put("default", naive);
        }

        this.builtin = new BuiltinImpl(invokerMap);
    }

    @Override
    public Path loadResource(String name, String resource) {
        Path path = resources.resolve(name);
        Local.loadResource(path, resource);
        return path;
    }

    @Override
    public void cleanTemp() {
        FilesUtils.assureEmptyDir(temp);
    }

    @Override
    public Path createTempFile(String prefix, String suffix, FileAttribute<?>... attrs) {
        try {
            return Files.createTempFile(temp, prefix, suffix, attrs);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "can't create temp file", e);
        }
        return null;
    }

    @Override
    public Path createTempExe() {
        Path exe = createTempFile("runnable", new OSRelatedValue<String>().windows(".exe").orElse(".out"));
        FilesUtils.chmod(exe, 777);
        return exe;
    }

    @Override
    public Path createTempDir() {
        try {
            return Files.createTempDirectory(temp, "dir");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "can't create temp directory", e);
        }
        return null;
    }

    @Override
    public CompilationResult compile(Language language, Path path, String... strings) {
        return compile(language.compiler(), path, strings);
    }

    @Override
    public CompilationResult compile(Compiler compiler, Path source, String... defines) {
        return compiler.compile(createTempExe(), source, defines);
    }

    @Override
    public CompilationResult compile(Source source, String... strings) {
        return compile(source.getLanguage().compiler(), source.getPath(), strings);
    }

    @Override
    public Languages getLanguages() {
        return languages;
    }

    @Override
    public RunInfo invokeDefault(RunOptions runOptions) {
        Invoker def = builtin.invoker("default");
        if (def == null) throw new UnsupportedOperationException("can't find default invoker");
        return invoke(def, runOptions);
    }

    @Override
    public RunInfo invoke(Invoker invoker, RunOptions runOptions) {
        return invoker.run(this, runOptions);
    }

    @Override
    public JValuerBuiltin builtin() {
        return builtin;
    }

    public UserAccount getDefaultAccount() {
        return defaultAccount;
    }
}
