package com.petukhovsky.jvaluer;

import java.nio.file.Path;

/**
 * Created by Arthur on 12/25/2015.
 */
public class GeneratedData extends PathData {

    private RunInfo info;

    public GeneratedData(Path path, RunInfo info) {
        super(path);
        this.info = info;
    }

    public RunInfo getInfo() {
        return info;
    }
}
