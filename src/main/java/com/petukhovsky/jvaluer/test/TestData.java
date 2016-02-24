package com.petukhovsky.jvaluer.test;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Created by Arthur on 12/21/2015.
 */
public abstract class TestData {
    abstract public boolean exists();

    abstract public InputStream openInputStream();

    abstract public Path getPath();

    abstract public String getString();
}
