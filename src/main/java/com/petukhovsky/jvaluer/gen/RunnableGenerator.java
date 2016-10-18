package com.petukhovsky.jvaluer.gen;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.data.GeneratedData;
import com.petukhovsky.jvaluer.commons.data.TestData;
import com.petukhovsky.jvaluer.commons.exe.Executable;
import com.petukhovsky.jvaluer.commons.gen.Generator;
import com.petukhovsky.jvaluer.commons.run.InvocationResult;
import com.petukhovsky.jvaluer.commons.run.RunInOut;
import com.petukhovsky.jvaluer.commons.run.RunLimits;
import com.petukhovsky.jvaluer.run.Runner;
import com.petukhovsky.jvaluer.run.RunnerBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Created by arthur on 18.10.16.
 */
public class RunnableGenerator implements Generator {

    private final Executable executable;
    private final RunnerBuilder builder;
    private final JValuer jValuer;

    public RunnableGenerator(JValuer jValuer, Executable executable, RunInOut inOut) {
        this.executable = executable;
        this.jValuer = jValuer;
        this.builder = new RunnerBuilder(jValuer).inOut(inOut);
    }

    @Override
    public GeneratedData generate(TestData testData, RunLimits runLimits, String... args) {
        try (Runner runner = builder.limits(runLimits).build(executable)) {
            InvocationResult result = runner.run(testData, args);
            Path copyTo = jValuer.createTempFile("", ".gen");
            Files.copy(result.getOut().getPath(), copyTo, StandardCopyOption.REPLACE_EXISTING);
            return new GeneratedData(copyTo, result.getRun());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
