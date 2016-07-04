package com.petukhovsky.jvaluer.run;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.local.UserAccount;
import com.petukhovsky.jvaluer.commons.run.RunInOut;
import com.petukhovsky.jvaluer.commons.run.RunLimits;
import com.petukhovsky.jvaluer.commons.run.RunOptions;
import com.petukhovsky.jvaluer.invoker.DefaultInvoker;
import com.petukhovsky.jvaluer.invoker.Invoker;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arthur Petukhovsky on 5/21/2016.
 */
public class RunnerBuilder {

    private final Path dir;
    private final JValuer jValuer;
    private final Map<String, Object> custom;
    private RunOptions options;
    private Invoker invoker;
    private RunInOut inOut;

    public RunnerBuilder(Path dir, JValuer jValuer) {
        this.dir = dir;
        this.jValuer = jValuer;
        this.options = new RunOptions().setFolder(dir);
        this.invoker = new DefaultInvoker();
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

    public RunnerBuilder invoker(Invoker invoker) {
        this.invoker = invoker;
        return this;
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

    public Runner build(Path exe) {
        return new Runner(jValuer, dir, exe, options.setCustom(custom), invoker, inOut);
    }
}
