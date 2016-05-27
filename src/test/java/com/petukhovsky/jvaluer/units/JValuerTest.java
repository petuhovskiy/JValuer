package com.petukhovsky.jvaluer.units;

import com.petukhovsky.jvaluer.JValuer;
import com.petukhovsky.jvaluer.JValuerBuilder;
import com.petukhovsky.jvaluer.compiler.CloneCompiler;
import com.petukhovsky.jvaluer.compiler.RunnableCompiler;
import com.petukhovsky.jvaluer.invoker.PythonInvoker;
import com.petukhovsky.jvaluer.lang.Language;
import com.petukhovsky.jvaluer.lang.Languages;
import com.petukhovsky.jvaluer.local.OSRelatedValue;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Created by Arthur Petukhovsky on 5/22/2016.
 */
public class JValuerTest {
    private JValuer jValuer;

    public JValuer loadJValuer() {
        return new JValuerBuilder()
                .addLanguage(new Language("GNU C++", new RunnableCompiler("g++", "{defines} -O2 -o {output} {source}")),
                        new String[]{}, new String[]{"c++", "cpp"})
                .addLanguage(new Language("GNU C++11", new RunnableCompiler("g++", "{defines} -O2 -o {output} {source}")),
                        new String[]{"cpp"}, new String[]{"c++11", "cpp11"})
                .addLanguage(new Language("Python 3", new CloneCompiler(),
                                new PythonInvoker(new OSRelatedValue<String>().windows("c:/Programs/Python-3/python.exe").orElse("python3"))),
                        new String[]{"py"}, new String[]{"python", "py", "python3"})
                .setPath(Paths.get("jvaluer-storage"))
                .build();
    }

    @Before
    public void before() {
        this.jValuer = loadJValuer();
    }

    @Test
    public void testLanguages() {
        Languages languages = loadJValuer().getLanguages();
        assertEquals(languages.findByExtension("py").name(), "Python 3");
        assertEquals(languages.findByName("c++11").name(), "GNU C++11");
    }
}
