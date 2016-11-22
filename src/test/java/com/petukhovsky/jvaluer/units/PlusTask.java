package com.petukhovsky.jvaluer.units;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.compiler.CompilationResult;
import com.petukhovsky.jvaluer.commons.data.StringData;
import com.petukhovsky.jvaluer.commons.exe.Executable;
import com.petukhovsky.jvaluer.commons.invoker.Invoker;
import com.petukhovsky.jvaluer.commons.run.*;
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
 * Created by petuh on 2/2/2016.
 */
public class PlusTask {

    private static final Logger logger = Logger.getLogger(PlusTask.class.getName());

    private JValuer jValuer;

    private Source source;
    private Path exe;

    @Before
    public void loadResources() {
        this.jValuer = new JValuerTest().loadJValuer();
        this.source = jValuer.getLanguages().autoSource(jValuer.loadResource("plus.cpp", "/plustxt.cpp"));
        exe = Utils.compileAssert(jValuer, source);
    }

    @Test
    public void testRunexe() throws IOException {
        Invoker invoker = jValuer.builtin().invoker("runexe");
        if (invoker == null) {
            logger.info("Runexe plustask test skip");
            return;
        }
        SafeRunner runner = Utils.trustedSafe(jValuer, RunLimits.unlimited(), RunInOut.txt(), exe, invoker);
        tests20(runner);
    }

    @Test
    public void testNaive() throws IOException {
        SafeRunner runner = Utils.trustedSafe(jValuer, RunLimits.unlimited(), RunInOut.txt(), exe, new NaiveInvoker());
        tests20(runner);
    }

    private void tests20(SafeRunner runner) {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int a = random.nextInt(100500);
            int b = random.nextInt(100500);
            int result = a + b;
            InvocationResult ir = runner.run(new StringData(a + " " + b));
            RunInfo info = ir.getRun();
            assertTrue(info.getRunVerdict() == RunVerdict.SUCCESS);
            assertEquals(ir.getOut().getString(), Integer.toString(result));
        }
    }


}
