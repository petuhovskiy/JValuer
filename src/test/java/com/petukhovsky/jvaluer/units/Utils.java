package com.petukhovsky.jvaluer.units;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.commons.compiler.CompilationResult;

import java.nio.file.Path;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * Created by arthur on 22.9.16.
 */
public class Utils {
    private static final Logger logger = Logger.getLogger(Utils.class.getName());

    public static Path compileAssert(JValuer jValuer, Path source) {
        CompilationResult result = jValuer.compile(source);
        logger.info(result.toString());
        assertTrue(result.isSuccess());
        return result.getExe();
    }
}
