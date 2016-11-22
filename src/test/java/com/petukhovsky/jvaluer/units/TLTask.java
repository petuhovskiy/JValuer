package com.petukhovsky.jvaluer.units;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.compiler.CompilationResult;
import com.petukhovsky.jvaluer.commons.data.StringData;
import com.petukhovsky.jvaluer.commons.exe.Executable;
import com.petukhovsky.jvaluer.commons.invoker.Invoker;
import com.petukhovsky.jvaluer.commons.run.RunInfo;
import com.petukhovsky.jvaluer.commons.run.RunLimits;
import com.petukhovsky.jvaluer.commons.run.RunVerdict;
import com.petukhovsky.jvaluer.commons.source.Source;
import com.petukhovsky.jvaluer.invoker.NaiveInvoker;
import com.petukhovsky.jvaluer.invoker.RunexeInvoker;
import com.petukhovsky.jvaluer.run.Runner;
import com.petukhovsky.jvaluer.run.RunnerBuilder;
import com.petukhovsky.jvaluer.run.SafeRunner;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Arthur Petukhovsky on 5/23/2016.
 */
public class TLTask {
    private static final Logger logger = Logger.getLogger(TLTask.class.getName());

    public static RunLimits ONE_SECOND = RunLimits.ofTime(1000L);

    private JValuer jValuer;

    private Source source;
    private Source source1;
    private Path exe;
    private Path exe1;

    @Before
    public void loadResources() {
        this.jValuer = new JValuerTest().loadJValuer();
        this.source = jValuer.getLanguages().autoSource(jValuer.loadResource("tl.cpp", "/tl.cpp"));
        this.source1 = jValuer.getLanguages().autoSource(jValuer.loadResource("while1.cpp", "/whiletrue.cpp"));
        exe = Utils.compileAssert(jValuer, source);
        exe1 = Utils.compileAssert(jValuer, source1);
    }

    @Test
    public void testRunexe() throws IOException {
        Invoker invoker = jValuer.builtin().invoker("runexe");
        if (invoker == null) {
            logger.info("Runexe tl test skip");
            return;
        }
        SafeRunner runner = Utils.trustedSafe(jValuer, ONE_SECOND, exe, invoker);
        tests(runner);
    }

    @Test
    public void testRunexeWhileTrue() throws IOException {
        Invoker invoker = jValuer.builtin().invoker("runexe");
        if (invoker == null) {
            logger.info("Runexe tl test skip");
            return;
        }
        SafeRunner runner = Utils.trustedSafe(jValuer, ONE_SECOND, exe1, invoker);
        for (int i = 0; i < 5; i++) {
            assertEquals(runner.run(new StringData("")).getRun().getRunVerdict(), RunVerdict.TIME_LIMIT_EXCEEDED);
        }
    }

    @Test
    public void testNaive() throws IOException {
        tests(Utils.trustedSafe(jValuer, ONE_SECOND, exe, new NaiveInvoker()));
    }

    private void tests(SafeRunner runner) {
        for (int i = 0; i < 2; i++) {
            RunInfo info = runner.run(new StringData(Integer.toString(12 + 34 * i))).getRun();
            assertTrue(info.getRunVerdict() == RunVerdict.SUCCESS);
        }
        for (int i = 0; i < 2; i++) {
            RunInfo info = runner.run(new StringData(Long.toString(123456789123456L + i))).getRun();
            assertTrue(info.getRunVerdict() == RunVerdict.TIME_LIMIT_EXCEEDED);
        }
    }
}
