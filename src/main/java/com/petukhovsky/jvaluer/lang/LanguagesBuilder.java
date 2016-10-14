package com.petukhovsky.jvaluer.lang;

import com.petukhovsky.jvaluer.commons.lang.Language;
import com.petukhovsky.jvaluer.commons.lang.Languages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Arthur Petukhovsky on 5/21/2016.
 */
public class LanguagesBuilder {

    private final Map<String, Language> names;
    private final Map<String, Language> exts;
    private final List<Language> list;

    public LanguagesBuilder() {
        this.names = new HashMap<>();
        this.exts = new HashMap<>();
        this.list = new ArrayList<>();
    }

    public Languages build() {
        return new LanguagesImpl(names, exts, list);
    }

    public LanguagesBuilder addLanguage(Language language, String[] ext, String[] name) {
        if (ext != null)
            for (String s : ext) if (exts.containsKey(s)) throw new RuntimeException("Extension duplicate");
        if (name != null) for (String s : name) if (names.containsKey(s)) throw new RuntimeException("Name duplicate");
        if (ext != null) for (String s : ext) exts.put(s, language);
        if (name != null) for (String s : name) names.put(s, language);
        list.add(language);
        return this;
    }
}
