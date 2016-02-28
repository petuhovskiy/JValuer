package com.petukhovsky.jvaluer.task;

import com.petukhovsky.jvaluer.checker.Checker;
import com.petukhovsky.jvaluer.run.Runner;
import com.petukhovsky.jvaluer.test.PathData;
import com.petukhovsky.jvaluer.test.TestVerdict;
import com.petukhovsky.jvaluer.util.Callback;
import com.petukhovsky.jvaluer.value.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by petuh on 2/24/2016.
 */
public class TaskModel {
    private TestGroup[] tests;
    private Checker checker = null;
    private String timeLimit = null;
    private String memoryLimit = null;

    public TaskModel(TestGroup[] tests) {
        this.tests = tests;
    }

    public TaskModel(TestGroup[] tests, Checker checker, String timeLimit, String memoryLimit) {
        this.tests = tests;
        this.checker = checker;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
    }

    public static TaskModel importFromFolder(Path folder, Checker checker, String timeLimit, String memoryLimit) throws IOException {
        List<Test> list = new ArrayList<>();
        Files.list(folder).forEach(path -> {
            String name = path.getFileName().toString();
            if (name.endsWith(".i")) {
                name = name.substring(0, name.length() - 2);
                Path out = path.getParent().resolve(name + ".o");
                if (Files.exists(out)) {
                    list.add(new Test(new PathData(path), new PathData(out), name));
                    return;
                }
            }

            name = path.getFileName().toString();
            if (name.endsWith(".in")) {
                name = name.substring(0, name.length() - 3);
                Path out = path.getParent().resolve(name + ".out");
                if (Files.exists(out)) {
                    list.add(new Test(new PathData(path), new PathData(out), name));
                    return;
                }
            }

            name = path.getFileName().toString();
            if (name.contains("in")) {
                Path out = path.getParent().resolve(name.replaceAll("in", "out"));
                name = name.replaceAll("in", "XX");
                if (Files.exists(out)) {
                    list.add(new Test(new PathData(path), new PathData(out), name));
                    return;
                }
            }

            name = path.getFileName().toString();
            Path out = path.getParent().resolve(name + ".a");
            if (Files.exists(out)) {
                list.add(new Test(new PathData(path), new PathData(out), name));
                return;
            }

            name = path.getFileName().toString();
            if (name.contains(".")) {
                String test = name.substring(name.lastIndexOf(".") + 1);
                name = name.substring(0, name.lastIndexOf("."));
                if (name.endsWith(".in")) {
                    out = path.getParent().resolve(name.substring(0, name.length() - 3) + ".out." + test);
                    if (Files.exists(out)) {
                        list.add(new Test(new PathData(path), new PathData(out), name));
                        return;
                    }
                }
            }
        });
        Test[] tests = new Test[list.size()];
        tests = list.toArray(tests);
        TestGroup testGroup = new TestGroup(tests);
        return new TaskModel(new TestGroup[]{testGroup}, checker, timeLimit, memoryLimit);
    }

    public Value checkAll(Runner runner, Callback<TestVerdict> callback) {
        //TODO
    }
}
