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

import static org.junit.Assert.assertTrue;

/**
 * Created by Arthur Petukhovsky on 5/23/2016.
 */
public class MLTask {
    private static final Logger logger = Logger.getLogger(MLTask.class.getName());

    private JValuer jValuer;

    private Source source;
    private Path exe;

    @Before
    public void loadResources() {
        this.jValuer = new JValuerTest().loadJValuer();
        this.source = jValuer.getLanguages().autoSource(jValuer.loadResource("ml.cpp", "/ml.cpp"));
        exe = Utils.compileAssert(jValuer, source);
    }

    @Test
    public void testRunexe() throws IOException {
        Invoker invoker = jValuer.builtin().invoker("default");
        if (!(invoker instanceof RunexeInvoker)) {
            logger.info("Runexe ml test skip");
            return;
        }
        final Long bytesGap = RunLimits.parseMemory("20mb");
        for (int i = 32; i <= 256; i *= 2) {
            SafeRunner runner = Utils.trustedSafe(jValuer, RunLimits.of("2s", i + "mb"), exe, invoker);
            Long bytes = RunLimits.parseMemory(i + "mb");

            RunInfo info = runner.run(new StringData("123")).getRun();
            logger.info(info.toString());
            assertTrue(info.getRunVerdict() == RunVerdict.SUCCESS);
            info = runner.run(new StringData(Long.toString((bytes - bytesGap) / 4))).getRun();
            logger.info(info.toString());
            assertTrue(info.getRunVerdict() == RunVerdict.SUCCESS);
            info = runner.run(new StringData(Long.toString(bytes / 2))).getRun();
            logger.info(info.toString());
            assertTrue(info.getRunVerdict() == RunVerdict.MEMORY_LIMIT_EXCEEDED);
        }
    }
}
