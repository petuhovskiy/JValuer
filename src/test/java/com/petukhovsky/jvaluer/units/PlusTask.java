package com.petukhovsky.jvaluer.units;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.compiler.CompilationResult;
import com.petukhovsky.jvaluer.run.RunInfo;
import com.petukhovsky.jvaluer.run.Runner;
import com.petukhovsky.jvaluer.test.StringData;
import com.petukhovsky.jvaluer.util.OSRelatedValue;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by petuh on 2/2/2016.
 */
public class PlusTask {

    private JValuer jValuer;

    private Path source;
    private Path exe;

    @Before
    public void loadResources() {
        this.jValuer = new JValuerTest().loadJValuer();
        this.source = jValuer.loadResource("plus.cpp", "/plustxt.cpp");
        CompilationResult result = jValuer.compile(source);
        assertTrue(result.isSuccess());
        exe = result.getExe();
    }

    @Test(timeout = 20000)
    public void testRunexe() throws IOException {
        if (new OSRelatedValue<Boolean>().windows(false).value().orElse(true)) {
            System.err.println("Runexe test skip");
            return;
        }
        try (Runner runner = jValuer.createRunner().setIn("input.txt")
                .setOut("output.txt")
                .addOption("login", "invoker")
                .addOption("password", "password")
                .build()) {
            runner.provideExecutable(exe);
            Random random = new Random();
            for (int i = 0; i < 20; i++) {
                int a = random.nextInt(100500);
                int b = random.nextInt(100500);
                int result = a + b;
                RunInfo info = runner.run(new StringData(a + " " + b));
                assertEquals(runner.getOutput().getString(), result + "");
                System.err.println(info);
            }

        }
    }
}
