package com.petukhovsky.jvaluer.units;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.compiler.CompilationResult;
import com.petukhovsky.jvaluer.invoker.RunexeInvoker;
import com.petukhovsky.jvaluer.local.OSRelatedValue;
import com.petukhovsky.jvaluer.run.RunInfo;
import com.petukhovsky.jvaluer.run.RunVerdict;
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
public class SecurityViolationTest {
    private static final Logger logger = Logger.getLogger(SecurityViolationTest.class.getName());

    private JValuer jValuer;

    private Path source;
    private Path exe;

    @Before
    public void loadResources() {
        this.jValuer = new JValuerTest().loadJValuer();
        this.source = jValuer.loadResource("security_violation.cpp", "/security_violation.cpp");
        CompilationResult result = jValuer.compile(source);
        logger.info(result + "");
        assertTrue(result.isSuccess());
        exe = result.getExe();
    }

    @Test
    public void testRunexeLinux() throws IOException {
        RunexeInvoker invoker = new RunexeInvoker();
        if (new OSRelatedValue<Boolean>().windows(true).unix(false).orElse(true)) {
            logger.info("Runexe security violation test skip");
            return;
        }
        //requires user with low permissions
        try (Runner runner = jValuer.createRunner()
                .setTimeLimit("1000ms")
                .setInvoker(invoker)
                .build()) {
            runner.provideExecutable(exe);
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
            RunInfo info = runner.run(new StringData(a + " " + b + " " + exe));
            assertTrue(info.getRunVerdict() == RunVerdict.SUCCESS);
        }
        for (int i = 0; i < 2; i++) {
            int a = 123;
            int b = random.nextInt(100);
            RunInfo info = runner.run(new StringData(a + " " + b + " " + exe));
            logger.severe(info + "");
            assertTrue(info.getRunVerdict() == RunVerdict.SECURITY_VIOLATION);
        }
    }
}
