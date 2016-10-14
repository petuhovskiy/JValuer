package com.petukhovsky.jvaluer.units;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.compiler.CompilationResult;
import com.petukhovsky.jvaluer.commons.data.StringData;
import com.petukhovsky.jvaluer.commons.invoker.Invoker;
import com.petukhovsky.jvaluer.commons.run.RunInfo;
import com.petukhovsky.jvaluer.commons.run.RunLimits;
import com.petukhovsky.jvaluer.commons.run.RunVerdict;
import com.petukhovsky.jvaluer.commons.source.Source;
import com.petukhovsky.jvaluer.invoker.NaiveInvoker;
import com.petukhovsky.jvaluer.invoker.RunexeInvoker;
import com.petukhovsky.jvaluer.run.Runner;
import com.petukhovsky.jvaluer.run.RunnerBuilder;
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

    private JValuer jValuer;

    private Source source;
    private Source source1;
    private Path exe;
    private Path exe1;

    @Before
    public void loadResources() {
        this.jValuer = new JValuerTest().loadJValuer();
        this.source = jValuer.getLanguages().autoSource(jValuer.loadResource("tl.cpp", "/tl.cpp"));
        this.source1 = jValuer.getLanguages().autoSource(jValuer.loadResource(".cpp", "/whiletrue.cpp"));
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
        try (Runner runner = new RunnerBuilder(jValuer)
                .limits(RunLimits.ofTime(1000L))
                .trusted()
                .invoker(invoker)
                .build(exe)) {
            tests(runner);
        }
    }

    @Test
    public void testRunexeWhileTrue() throws IOException {
        Invoker invoker = jValuer.builtin().invoker("runexe");
        if (invoker == null) {
            logger.info("Runexe tl test skip");
            return;
        }
        for (int i = 0; i < 5; i++) {
            try (Runner runner = new RunnerBuilder(jValuer)
                    .limits(RunLimits.ofTime(1000L))
                    .trusted()
                    .invoker(invoker)
                    .build(exe)) {
                assertEquals(runner.run(new StringData("")).getRun().getRunVerdict(), RunVerdict.TIME_LIMIT_EXCEEDED);
            }
        }
    }

    @Test
    public void testNaive() throws IOException {
        try (Runner runner = new RunnerBuilder(jValuer)
                .limits(RunLimits.ofTime(1000L))
                .invoker(new NaiveInvoker())
                .build(exe)) {
            tests(runner);
        }
    }

    private void tests(Runner runner) {
        Random random = new Random();
        for (int i = 0; i < 2; i++) {
            int a = random.nextInt(100);
            RunInfo info = runner.run(new StringData(a + "")).getRun();
            assertTrue(info.getRunVerdict() == RunVerdict.SUCCESS);
        }
        for (int i = 0; i < 2; i++) {
            long a = 123456789123456L + random.nextInt(100);
            RunInfo info = runner.run(new StringData(a + "")).getRun();
            assertTrue(info.getRunVerdict() == RunVerdict.TIME_LIMIT_EXCEEDED);
        }
    }
}
