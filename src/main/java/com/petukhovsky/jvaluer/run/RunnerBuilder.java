package com.petukhovsky.jvaluer.run;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.exe.Executable;
import com.petukhovsky.jvaluer.commons.invoker.Invoker;
import com.petukhovsky.jvaluer.commons.lang.Language;
import com.petukhovsky.jvaluer.commons.local.UserAccount;
import com.petukhovsky.jvaluer.commons.run.RunInOut;
import com.petukhovsky.jvaluer.commons.run.RunLimits;
import com.petukhovsky.jvaluer.commons.run.RunOptions;
import com.petukhovsky.jvaluer.commons.source.Source;
import com.petukhovsky.jvaluer.invoker.DefaultInvoker;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Arthur Petukhovsky on 5/21/2016.
 */
public class RunnerBuilder {

    private final JValuer jValuer;
    private final Map<String, Object> custom;
    private RunOptions options;
    private RunInOut inOut;

    public RunnerBuilder(JValuer jValuer) {
        this.jValuer = jValuer;
        this.options = new RunOptions();
        this.inOut = RunInOut.std();
        this.custom = new HashMap<>();
    }

    public RunnerBuilder custom(String key, Object value) {
        custom.put(key, value);
        return this;
    }

    public RunnerBuilder limits(RunLimits limits) {
        this.options = this.options.setLimits(limits);
        return this;
    }

    public RunnerBuilder inOut(RunInOut inOut) {
        this.inOut = inOut;
        return this;
    }

    public RunnerBuilder trusted(boolean trusted) {
        this.options = this.options.setTrusted(trusted);
        return this;
    }

    public RunnerBuilder trusted() {
        return trusted(true);
    }


    public RunnerBuilder injectDll(Path dll) {
        this.options = this.options.setDllInject(dll);
        return this;
    }

    public RunnerBuilder account(UserAccount userAccount) {
        this.options = this.options.setUserAccount(userAccount);
        return this;
    }

    public RunnerBuilder args(String args) {
        this.options.setArgs(args);
        return this;
    }

    public SafeRunner buildSafe(Executable executable) {
        Path exe = executable.getPath();
        Invoker invoker = executable.getInvoker();
        Objects.requireNonNull(exe);
        Objects.requireNonNull(invoker);
        return new SafeRunner(jValuer, exe, options.setCustom(custom), invoker, inOut);
    }

    public Runner build(Executable exe) {
        return build(exe.getPath(), exe.getInvoker());
    }

    public Runner build(Path exe, Invoker invoker) {
        Objects.requireNonNull(exe);
        Objects.requireNonNull(invoker);
        Path dir = jValuer.createTempDir();
        return new Runner(jValuer, dir, exe, options.setCustom(custom), invoker, inOut);
    }

    public Runner build(Path exe, Language language) {
        return build(exe, language.invoker());
    }

    public Runner build(Path exe, Source source) {
        return build(exe, source.getLanguage());
    }
}
