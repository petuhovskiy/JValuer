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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by petuh on 2/2/2016.
 */
public class PlusTask {

    private static final Logger logger = Logger.getLogger(PlusTask.class.getName());

    private JValuer jValuer;

    private Path source;
    private Path exe;

    @Before
    public void loadResources() {
        this.jValuer = new JValuerTest().loadJValuer();
        this.source = jValuer.loadResource("plus.cpp", "/plustxt.cpp");
        CompilationResult result = jValuer.compile(source);
        logger.info(result + "");
        assertTrue(result.isSuccess());
        exe = result.getExe();
    }

    @Test(timeout = 20000)
    public void testRunexe() throws IOException {
        RunexeInvoker invoker = new RunexeInvoker();
        if (!invoker.isSupported(jValuer)) {
            logger.info("Runexe plustask test skip");
            return;
        }
        try (Runner runner = jValuer.createRunner()
                .setIn("input.txt")
                .setOut("output.txt")
                .setTrusted()
                .setInvoker(invoker)
                .build()) {
            runner.provideExecutable(exe);
            tests20(runner);
        }
    }

    @Test(timeout = 20000)
    public void testNaive() throws IOException {
        try (Runner runner = jValuer.createRunner()
                .setIn("input.txt")
                .setOut("output.txt")
                .setInvoker(new NaiveInvoker())
                .build()) {
            runner.provideExecutable(exe);
            tests20(runner);
        }
    }

    private void tests20(Runner runner) {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int a = random.nextInt(100500);
            int b = random.nextInt(100500);
            int result = a + b;
            RunInfo info = runner.run(new StringData(a + " " + b));
            assertTrue(info.getRunVerdict() == RunVerdict.SUCCESS);
            assertEquals(runner.getOutput().getString(), result + "");
        }
    }

}
