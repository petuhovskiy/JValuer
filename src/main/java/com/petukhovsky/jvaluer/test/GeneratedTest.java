package com.petukhovsky.jvaluer.test;

import com.petukhovsky.jvaluer.RunInfo;

import java.nio.file.Path;

/**
 * Created by Arthur on 12/25/2015.
 */
public class GeneratedTest extends PathTest {

    private RunInfo info;

    public GeneratedTest(Path path, RunInfo info) {
        super(path);
        this.info = info;
    }

    public RunInfo getInfo() {
        return info;
    }
}
