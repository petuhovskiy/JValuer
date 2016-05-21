package com.petukhovsky.jvaluer;

import com.petukhovsky.jvaluer.lang.Language;
import com.petukhovsky.jvaluer.lang.LanguagesBuilder;

import java.nio.file.Path;

/**
 * Created by Arthur Petukhovsky on 5/21/2016.
 */
public class JValuerBuilder {

    LanguagesBuilder languagesBuilder;
    Path path;

    public JValuerBuilder() {
        languagesBuilder = new LanguagesBuilder();
        path = null;
    }

    public JValuerBuilder addLanguage(Language language, String[] exts, String[] names) {
        languagesBuilder.addLanguage(language, exts, names);
        return this;
    }

    public JValuerBuilder setPath(Path path) {
        this.path = path;
        return this;
    }

    public JValuer build() {
        if (path == null) throw new RuntimeException("Path is missing");
        return new JValuer(languagesBuilder.build(), path);
    }

}
