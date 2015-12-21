package com.petukhovsky.jvaluer.app;

import com.petukhovsky.jvaluer.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by Arthur on 12/18/2015.
 */
public class TestsChecker {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Executable filename: ");
        String exe = sc.nextLine().trim();
        System.out.print("Folder with tests: ");
        String folder = sc.nextLine().trim();
        System.out.print("Time limit: ");
        String time = sc.nextLine().trim();
        System.out.print("Memory limit: ");
        String memory = sc.nextLine().trim();

        RunOptions options = new RunOptions("trusted", "");
        if (!time.isEmpty()) options.append("time_limit", time);
        if (!memory.isEmpty()) options.append("memory_limit", memory);

        Runner runner = new Runner("input.txt", "output.txt", new WindowsInvoker(), options);
        Grader grader = new Grader(runner, new Estimator(new TokenChecker()));
        grader.provideExecutable(Paths.get(exe));

        Path tests = Paths.get(folder);
        Files.list(tests).forEach(path -> {
            String name = path.getFileName().toString();
            if (name.endsWith(".i")) {
                name = name.substring(0, name.length() - 2);
                Path out = path.getParent().resolve(name + ".o");
                if (Files.exists(out)) printResult(name, grader.test(path, out));
            }
            name = path.getFileName().toString();
            if (name.endsWith(".in")) {
                name = name.substring(0, name.length() - 3);
                Path out = path.getParent().resolve(name + ".out");
                if (Files.exists(out)) printResult(name, grader.test(path, out));
            }
            name = path.getFileName().toString();
            Path out = path.getParent().resolve(name + ".a");
            if (Files.exists(out)) printResult(name, grader.test(path, out));
            name = path.getFileName().toString();
            if (name.contains(".")) {
                String test = name.substring(name.lastIndexOf(".") + 1);
                name = name.substring(0, name.lastIndexOf("."));
                if (name.endsWith(".in")) {
                    out = path.getParent().resolve(name.substring(0, name.length() - 3) + ".out." + test);
                    if (Files.exists(out)) printResult(test, grader.test(path, out));
                }
            }
        });
    }

    private static void printResult(String name, TestVerdict test) {
        System.out.println("Test " + name + " (" + test.getTime() + ", " + test.getMemory() + ") - " + test.getVerdict() + ". " + test.getComment());
    }
}
