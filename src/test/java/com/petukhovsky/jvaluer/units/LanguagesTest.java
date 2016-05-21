package com.petukhovsky.jvaluer.units;

import com.petukhovsky.jvaluer.compiler.CloneCompiler;
import com.petukhovsky.jvaluer.compiler.RunnableCompiler;
import com.petukhovsky.jvaluer.invoker.PythonInvoker;
import com.petukhovsky.jvaluer.lang.Language;
import com.petukhovsky.jvaluer.lang.Languages;
import com.petukhovsky.jvaluer.lang.LanguagesBuilder;
import com.petukhovsky.jvaluer.util.OSRelatedValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Arthur Petukhovsky on 5/21/2016.
 */
public class LanguagesTest {

    public Languages languages() {
        return new LanguagesBuilder()
                .addLanguage(new Language("GNU C++", new RunnableCompiler("g++", "{defines} -O2 -o {output} {source}")),
                        new String[]{}, new String[]{"c++", "cpp"})
                .addLanguage(new Language("GNU C++11", new RunnableCompiler("g++", "{defines} -O2 -o {output} {source}")),
                        new String[]{"cpp"}, new String[]{"c++11", "cpp11"})
                .addLanguage(new Language("Python 3", new CloneCompiler(),
                                new PythonInvoker(new OSRelatedValue<String>().windows("c:/Programs/Python-3/python.exe").value().orElse("python3"))),
                        new String[]{"py"}, new String[]{"python", "py", "python3"})
                .build();
    }

    @Test
    public void test() {
        Languages languages = languages();
        assertEquals(languages.findByExtension("py").name(), "Python 3");
        assertEquals(languages.findByName("c++11").name(), "GNU C++11");
    }
}
