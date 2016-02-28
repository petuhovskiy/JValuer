package com.petukhovsky.jvaluer.units;

import com.petukhovsky.jvaluer.Language;
import com.petukhovsky.jvaluer.compiler.CompilationResult;
import com.petukhovsky.jvaluer.run.RunInfo;
import com.petukhovsky.jvaluer.run.RunOptions;
import com.petukhovsky.jvaluer.run.RunVerdict;
import com.petukhovsky.jvaluer.run.Runner;
import com.petukhovsky.jvaluer.test.PathData;
import com.petukhovsky.jvaluer.test.StringData;
import com.petukhovsky.jvaluer.util.AtomScanner;
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
    @Test
    public void runTest() {
        Path sourcePath = null;
        try {
            sourcePath = Files.createTempFile("plus", ".cpp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (InputStream is = PlusTask.class.getResourceAsStream("/plustxt.cpp")) {
            Files.copy(is, sourcePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sourcePath.toFile().deleteOnExit();
        Runner runner = null;
        try {
            runner = new Runner("input.txt", "output.txt", new RunOptions("trusted", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(runner);
        Language language = Language.GNU_CPP11;
        CompilationResult result = null;
        try {
            result = language.compiler().compile(sourcePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        runner.provideExecutable(result.getExe());
        RunInfo info = runner.run(new StringData("5 8"));
        assertEquals(info.getExitCode(), 0);
        assertEquals(info.getRunVerdict(), RunVerdict.SUCCESS);
        PathData data = runner.getOutput();
        assertTrue(data.exists());
        AtomScanner scanner = new AtomScanner(data);
        assertEquals(13, scanner.nextInt());
        scanner.close();
    }

    @Test
    public void compileTest() {
        Path sourcePath = null;
        try {
            sourcePath = Files.createTempFile("plus", ".cpp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (InputStream is = PlusTask.class.getResourceAsStream("/plustxt.cpp")) {
            Files.copy(is, sourcePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sourcePath.toFile().deleteOnExit();
        Language language = Language.GNU_CPP11;
        CompilationResult result = null;
        try {
            result = language.compiler().compile(sourcePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    public void getSourceTest() {
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
    }

    @Test
    public void runTests() {
        Path sourcePath = null;
        try {
            sourcePath = Files.createTempFile("plus", ".cpp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (InputStream is = PlusTask.class.getResourceAsStream("/plustxt.cpp")) {
            Files.copy(is, sourcePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sourcePath.toFile().deleteOnExit();
        Runner runner = null;
        try {
            runner = new Runner("input.txt", "output.txt", new RunOptions("trusted", ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(runner);
        Language language = Language.GNU_CPP11;
        CompilationResult result = null;
        try {
            result = language.compiler().compile(sourcePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        runner.provideExecutable(result.getExe());
        Random random = new Random(1337);
        for (int i = 0; i < 50; i++) {
            int a = random.nextInt(1000000);
            int b = random.nextInt(1000000);
            RunInfo info = runner.run(new StringData(a + " " + b));
            assertEquals(info.getExitCode(), 0);
            assertEquals(info.getRunVerdict(), RunVerdict.SUCCESS);
            PathData data = runner.getOutput();
            assertTrue(data.exists());
            AtomScanner scanner = new AtomScanner(data);
            assertEquals(a + b, scanner.nextInt());
            scanner.close();
        }
    }

}
