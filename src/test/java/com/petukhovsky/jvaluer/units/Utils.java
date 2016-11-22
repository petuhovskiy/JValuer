package com.petukhovsky.jvaluer.units;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.compiler.CompilationResult;
import com.petukhovsky.jvaluer.commons.exe.Executable;
import com.petukhovsky.jvaluer.commons.invoker.Invoker;
import com.petukhovsky.jvaluer.commons.run.RunInOut;
import com.petukhovsky.jvaluer.commons.run.RunLimits;
import com.petukhovsky.jvaluer.commons.source.Source;
import com.petukhovsky.jvaluer.run.RunnerBuilder;
import com.petukhovsky.jvaluer.run.SafeRunner;

import java.nio.file.Path;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * Created by arthur on 22.9.16.
 */
public class Utils {
    private static final Logger logger = Logger.getLogger(Utils.class.getName());

    public static Path compileAssert(JValuer jValuer, Source source) {
        CompilationResult result = jValuer.compile(source);
        logger.info(result.toString());
        assertTrue(result.isSuccess());
        return result.getExe();
    }

    public static SafeRunner trustedSafe(JValuer jValuer, RunLimits limits, Path exe, Invoker invoker) {
        return trustedSafe(jValuer, limits, RunInOut.std(), exe, invoker);
    }

    public static SafeRunner trustedSafe(JValuer jValuer, RunLimits limits, RunInOut inOut, Path exe, Invoker invoker) {
        return new RunnerBuilder(jValuer)
                .inOut(inOut)
                .limits(limits)
                .trusted()
                .buildSafe(new Executable(exe, invoker));
    }
}
