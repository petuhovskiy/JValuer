package com.petukhovsky.jvaluer.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Created by Arthur on 12/21/2015.
 */
public class StringData extends TestData {

    String data;

    public StringData(String data) {
        this.data = data;
    }

    @Override
    public boolean exists() {
        return data != null;
    }

    @Override
    public InputStream openInputStream() {
        return new ByteArrayInputStream(data.getBytes());
    }

    @Override
    public Path getPath() {
        Path path = null;
        try {
            path = Files.createTempFile("", "");
            path.toFile().deleteOnExit();
            try (InputStream is = openInputStream()) {
                Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    @Override
    public String getString() {
        return data;
    }
}
