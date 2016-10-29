package com.petukhovsky.jvaluer;

import com.petukhovsky.jvaluer.commons.lang.Language;
import com.petukhovsky.jvaluer.commons.local.UserAccount;
import com.petukhovsky.jvaluer.impl.JValuerImpl;
import com.petukhovsky.jvaluer.lang.LanguagesBuilder;

import java.nio.file.Path;

/**
 * Created by Arthur Petukhovsky on 5/21/2016.
 */
public class JValuerBuilder {

    LanguagesBuilder languagesBuilder;
    Path path;
    UserAccount defaultAccount;

    public JValuerBuilder() {
        languagesBuilder = new LanguagesBuilder();
        path = null;
    }

    public JValuerBuilder addLanguage(Language language, String[] exts, String[] names) {
        languagesBuilder.addLanguage(language, exts, names);
        return this;
    }

    public JValuerBuilder setPath(Path path) {
        this.path = path.toAbsolutePath();
        return this;
    }

    public JValuerBuilder setAccount(UserAccount account) {
        this.defaultAccount = account;
        return this;
    }

    public JValuer build() {
        if (path == null) throw new RuntimeException("Path is missing");
        return new JValuerImpl(languagesBuilder.build(), path, defaultAccount);
    }

}
