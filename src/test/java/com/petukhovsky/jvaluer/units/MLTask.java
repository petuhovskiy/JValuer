package com.petukhovsky.jvaluer.units;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.compiler.CompilationResult;
import com.petukhovsky.jvaluer.commons.data.StringData;
import com.petukhovsky.jvaluer.commons.run.RunInfo;
import com.petukhovsky.jvaluer.commons.run.RunLimits;
import com.petukhovsky.jvaluer.commons.run.RunVerdict;
import com.petukhovsky.jvaluer.invoker.RunexeInvoker;
import com.petukhovsky.jvaluer.run.Runner;
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

    private Path source;
    private Path exe;

    @Before
    public void loadResources() {
        this.jValuer = new JValuerTest().loadJValuer();
        this.source = jValuer.loadResource("ml.cpp", "/ml.cpp");
        CompilationResult result = jValuer.compile(source);
        logger.info(result + "");
        assertTrue(result.isSuccess());
        exe = result.getExe();
    }

    @Test
    public void testRunexe() throws IOException {
        RunexeInvoker invoker = new RunexeInvoker();
        if (!invoker.isSupported(jValuer)) {
            logger.info("Runexe ml test skip");
            return;
        }
        Random random = new Random();
        final int mb = 1024 * 1024 / 4;
        for (int i = 64; i <= 256; i *= 2) {
            try (Runner runner = jValuer.createRunner()
                    .trusted()
                    .invoker(invoker)
                    .limits(RunLimits.ofMemory(1024L * 1024 * i))
                    .build(exe)) {
                int a = 5 + random.nextInt(100);
                RunInfo info = runner.run(new StringData(a + "")).getRun();
                logger.info(info + "");
                assertTrue(info.getRunVerdict() == RunVerdict.SUCCESS);
                a = (i - 13) * mb;
                info = runner.run(new StringData(a + "")).getRun();
                logger.info(info + "");
                assertTrue(info.getRunVerdict() == RunVerdict.SUCCESS);
                a = i * mb;
                info = runner.run(new StringData(a + "")).getRun();
                logger.info(info + "");
                assertTrue(info.getRunVerdict() == RunVerdict.MEMORY_LIMIT_EXCEEDED);
            }
        }
    }
}
