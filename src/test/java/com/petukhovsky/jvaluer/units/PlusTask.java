package com.petukhovsky.jvaluer.units;

import com.petukhovsky.jvaluer.compiler.CompilationResult;
import com.petukhovsky.jvaluer.lang.Language;
import com.petukhovsky.jvaluer.run.RunInfo;
import com.petukhovsky.jvaluer.run.RunVerdict;
import com.petukhovsky.jvaluer.run.Runner;
import com.petukhovsky.jvaluer.test.PathData;
import com.petukhovsky.jvaluer.test.StringData;
import com.petukhovsky.jvaluer.util.FastScanner;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by petuh on 2/2/2016.
 */
public class PlusTask {

    private Path getSourcePath() {
        Path sourcePath = null;
        try {
            sourcePath = Files.createTempFile("plus", ".cpp");
        } catch (IOException e) {
            fail("failed to create temp file");
        }
        assertNotNull(sourcePath);
        try (InputStream is = PlusTask.class.getResourceAsStream("/plustxt.cpp")) {
            Files.copy(is, sourcePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            fail("plus.cpp not found");
        }
        sourcePath.toFile().deleteOnExit();
        assertTrue(Files.exists(sourcePath));
        return sourcePath;
    }

    private Runner createRunner() {
        Runner runner = null;
        try {
            runner = new Runner();
            runner.setFiles("input.txt", "output.txt");
        } catch (IOException e) {
            e.printStackTrace();
            fail("failed to create runner");
        }
        return runner;
    }

    @Test
    public void runTest() {
        try (Runner runner = new Runner()) {
            runner.setFiles("input.txt", "output.txt");
            Path sourcePath = getSourcePath();
            Language language = Language.GNU_CPP11;
            CompilationResult result = language.compiler().compile(sourcePath);
            runner.provideExecutable(result.getExe());
            RunInfo info = runner.run(new StringData("5 8"));
            assertEquals(info.getExitCode(), 0);
            assertEquals(info.getRunVerdict(), RunVerdict.SUCCESS);
            PathData data = runner.getOutput();
            assertTrue(data.exists());
            try (FastScanner scanner = new FastScanner(data)) {
                assertEquals(13, scanner.nextInt());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void compileTest() {
        Path sourcePath = getSourcePath();
        Language language = Language.GNU_CPP11;
        CompilationResult result = language.compiler().compile(sourcePath);
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    public void getSourceTest() {
        getSourcePath();
    }

    @Test
    public void runTests() {
        try (Runner runner = new Runner()) {
            runner.setFiles("input.txt", "output.txt");
            Path sourcePath = getSourcePath();
            Language language = Language.GNU_CPP11;
            CompilationResult result = language.compiler().compile(sourcePath);
            runner.provideExecutable(result.getExe());
            Random random = new Random(1337);
            for (int i = 0; i < 20; i++) {
                int a = random.nextInt(1000000);
                int b = random.nextInt(1000000);
                RunInfo info = runner.run(new StringData(a + " " + b));
                assertEquals(info.getExitCode(), 0);
                assertEquals(info.getRunVerdict(), RunVerdict.SUCCESS);
                PathData data = runner.getOutput();
                assertTrue(data.exists());
                try (FastScanner scanner = new FastScanner(data)) {
                    assertEquals(a + b, scanner.nextInt());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail("failed");
        }
    }

}
