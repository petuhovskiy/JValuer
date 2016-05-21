package com.petukhovsky.jvaluer.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Arthur Petukhovsky on 5/21/2016.
 */
public class LanguagesBuilder {

    private Map<String, Language> names;
    private Map<String, Language> exts;
    private List<Language> list;

    public LanguagesBuilder() {
        this.names = new HashMap<>();
        this.exts = new HashMap<>();
        this.list = new ArrayList<>();
    }

    public Languages build() {
        return new Languages(names, exts, list);
    }

    public LanguagesBuilder addLanguage(Language language, String[] ext, String[] name) {
        for (String s : ext) if (exts.containsKey(s)) throw new RuntimeException("Extension duplicate");
        for (String s : name) if (names.containsKey(s)) throw new RuntimeException("Name duplicate");
        for (String s : ext) exts.put(s, language);
        for (String s : name) names.put(s, language);
        list.add(language);
        return this;
    }
}
