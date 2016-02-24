package com.petukhovsky.jvaluer.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Arthur on 12/21/2015.
 */
public class PathData extends TestData {

    private Path path;

    public PathData(Path path) {
        this.path = path;
    }

    @Override
    public boolean exists() {
        return path != null && Files.exists(path);
    }

    @Override
    public InputStream openInputStream() {
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public String getString() {
        try {
            return String.join("\n", Files.readAllLines(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
