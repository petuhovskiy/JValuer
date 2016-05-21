package com.petukhovsky.jvaluer.lang;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by Arthur Petukhovsky on 5/21/2016.
 */
public class Languages {
    private final Map<String, Language> names;
    private final Map<String, Language> exts;
    private final List<Language> all;

    public Languages(Map<String, Language> names, Map<String, Language> exts, List<Language> all) {
        this.names = names;
        this.exts = exts;
        this.all = all;
    }

    public Language findByExtension(String ext) {
        return exts.get(ext);
    }

    public Language findByName(String name) {
        return names.get(name);
    }

    public Language findByPath(Path path) {
        String name = path.getFileName().toString();
        int pos = name.lastIndexOf('.');
        if (pos == -1) return null;
        return findByExtension(name.substring(pos + 1));
    }

    public List<Language> getAll() {
        return all;
    }

}
