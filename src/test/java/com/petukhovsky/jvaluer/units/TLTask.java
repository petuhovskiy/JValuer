package com.petukhovsky.jvaluer.units;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.compiler.CompilationResult;
import com.petukhovsky.jvaluer.invoker.NaiveInvoker;
import com.petukhovsky.jvaluer.invoker.RunexeInvoker;
import com.petukhovsky.jvaluer.run.RunInfo;
import com.petukhovsky.jvaluer.run.Runner;
import com.petukhovsky.jvaluer.test.StringData;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * Created by Arthur Petukhovsky on 5/23/2016.
 */
public class TLTask {
    private static final Logger logger = Logger.getLogger(TLTask.class.getName());

    private JValuer jValuer;

    private Path source;
    private Path exe;

    @Before
    public void loadResources() {
        this.jValuer = new JValuerTest().loadJValuer();
        this.source = jValuer.loadResource("tl.cpp", "/tl.cpp");
        CompilationResult result = jValuer.compile(source);
        logger.info(result + "");
        assertTrue(result.isSuccess());
        exe = result.getExe();
    }

    @Test
    public void testRunexe() throws IOException {
        RunexeInvoker invoker = new RunexeInvoker();
        if (!invoker.isSupported(jValuer)) {
            logger.info("Runexe tl test skip");
            return;
        }
        try (Runner runner = jValuer.createRunner()
                .setTimeLimit("1000ms")
                .setTrusted()
                .setInvoker(invoker)
                .build()) {
            runner.provideExecutable(exe);
            tests(runner);
        }
    }

    @Test
    public void testNaive() throws IOException {
        try (Runner runner = jValuer.createRunner()
                .setTimeLimit("1000ms")
                .setInvoker(new NaiveInvoker())
                .build()) {
            runner.provideExecutable(exe);
            tests(runner);
        }
    }

    private void tests(Runner runner) {
        Random random = new Random();
        for (int i = 0; i < 2; i++) {
            int a = random.nextInt(100);
            RunInfo info = runner.run(new StringData(a + ""));
            assertTrue(info.getRunVerdict() == RunVerdict.SUCCESS);
        }
        for (int i = 0; i < 2; i++) {
            long a = 123456789123456L + random.nextInt(100);
            RunInfo info = runner.run(new StringData(a + ""));
            assertTrue(info.getRunVerdict() == RunVerdict.TIME_LIMIT_EXCEEDED);
        }
    }
}
