package com.petukhovsky.jvaluer.units;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.compiler.CompilationResult;
import com.petukhovsky.jvaluer.commons.data.StringData;
import com.petukhovsky.jvaluer.commons.invoker.Invoker;
import com.petukhovsky.jvaluer.commons.local.OSRelatedValue;
import com.petukhovsky.jvaluer.commons.run.RunInfo;
import com.petukhovsky.jvaluer.commons.run.RunLimits;
import com.petukhovsky.jvaluer.commons.run.RunVerdict;
import com.petukhovsky.jvaluer.commons.source.Source;
import com.petukhovsky.jvaluer.invoker.RunexeInvoker;
import com.petukhovsky.jvaluer.run.Runner;
import com.petukhovsky.jvaluer.run.RunnerBuilder;
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
public class SecurityViolationTest {
    private static final Logger logger = Logger.getLogger(SecurityViolationTest.class.getName());

    private JValuer jValuer;

    private Source source;
    private Path exe;

    @Before
    public void loadResources() {
        this.jValuer = new JValuerTest().loadJValuer();
        this.source = jValuer.getLanguages().autoSource(jValuer.loadResource("security_violation.cpp", "/security_violation.cpp"));
        CompilationResult result = jValuer.compile(source);
        logger.info(result + "");
        assertTrue(result.isSuccess());
        exe = result.getExe();
    }

    @Test
    public void testRunexeLinux() throws IOException {
        Invoker invoker = jValuer.builtin().invoker("runexe");
        if (invoker == null) {
            logger.info("Runexe security violation test skip");
            return;
        }
        //requires user with low permissions
        try (Runner runner = new RunnerBuilder(jValuer)
                .limits(RunLimits.ofTime(1000L))
                .invoker(invoker)
                .build(exe)) {
            tests(runner);
        }
    }

    private void tests(Runner runner) {
        String exe = new OSRelatedValue<String>()
                .windows("notepad.exe")
                .unix("subl")
                .orElse(null);
        Random random = new Random();
        for (int i = 0; i < 2; i++) {
            int a = random.nextInt(5);
            int b = random.nextInt(5);
            RunInfo info = runner.run(new StringData(a + " " + b + " " + exe)).getRun();
            assertTrue(info.getRunVerdict() == RunVerdict.SUCCESS);
        }
        for (int i = 0; i < 2; i++) {
            int a = 123;
            int b = random.nextInt(100);
            RunInfo info = runner.run(new StringData(a + " " + b + " " + exe)).getRun();
            logger.severe(info + "");
            assertTrue(info.getRunVerdict() == RunVerdict.SECURITY_VIOLATION);
        }
    }
}
