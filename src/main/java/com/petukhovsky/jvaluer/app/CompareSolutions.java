package com.petukhovsky.jvaluer.app;

import com.petukhovsky.jvaluer.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Scanner;

/**
 * Created by Arthur on 12/26/2015.
 */
public class CompareSolutions {
    public static void main(String[] args) throws IOException {
        System.out.println("Solutions comparator v0.2. Defaults: TokenChecker, 100 tests, stdin, stdout.");
        Scanner sc = new Scanner(System.in);
        System.out.print("Generator source: ");
        String genSource = sc.nextLine().trim();
        System.out.print("Model solution: ");
        String source1 = sc.nextLine().trim();
        System.out.print("Test solution: ");
        String source2 = sc.nextLine().trim();
        System.out.print("Count: ");
        String countString = sc.nextLine().trim();
        int count = countString.isEmpty() ? 100 : Integer.parseInt(countString);
        System.out.print("Test pattern: ");
        String pattern = sc.nextLine().trim();
        System.out.print("Checker: ");
        String checkerSource = sc.nextLine().trim();

        Checker checker;
        if (checkerSource.isEmpty()) checker = new TokenChecker();
        else checker = new TestlibChecker(Paths.get(checkerSource));

        Language language = Language.GNU_CPP11;

        CompilationResult compiledGen = language.compile(Paths.get(genSource));
        if (!compiledGen.isSuccess()) {
            System.out.println("Error while compiling");
            System.out.println(compiledGen.getComment());
            return;
        }
        System.out.println("Compiled successfully");

        CompilationResult compiled1 = language.compile(Paths.get(source1));
        if (!compiled1.isSuccess()) {
            System.out.println("Error while compiling");
            System.out.println(compiledGen.getComment());
            return;
        }
        System.out.println("Compiled successfully");

        Grader grader = new Grader(new Runner("stdin", "stdout", new RunOptions("trusted", "").append("time_limit", "5s").append("memory_limit", "512m")), new Estimator(checker));
        Runner runner = new Runner("stdin", "stdout", new RunOptions("trusted", ""));
        runner.provideExecutable(compiled1.getExe());

        Generator generator = new Generator(compiledGen.getExe());

        while (true) {
            CompilationResult compiled2 = language.compile(Paths.get(source2));
            if (!compiled2.isSuccess()) {
                System.out.println("Error while compiling");
                System.out.println(compiledGen.getComment());
                return;
            }
            System.out.println("Compiled successfully");

            grader.provideExecutable(compiled2.getExe());

            for (int i = 1; i <= count; i++) {
                GeneratedData test = generator.generate(pattern.replaceAll("\\$", String.valueOf(i)));
                RunInfo info = test.getInfo();
                System.out.println("Test " + i + " generated: " + info.getPassedTime() + "ms, exitcode " + info.getExitCode());
                System.out.println("Model solution: " + new TestVerdict(new CheckResult(true, "ok"), runner.run(test), "Accepted"));
                TestData answer = runner.getOutput();
                TestVerdict verdict = grader.test(test, answer);
                System.out.println("Grade solution: " + verdict);
                if (!verdict.isAccepted()) {
                    try (InputStream is = test.openInputStream()) {
                        Files.copy(is, Paths.get("in.txt"), StandardCopyOption.REPLACE_EXISTING);
                    }
                    try (InputStream is = answer.openInputStream()) {
                        Files.copy(is, Paths.get("answer.txt"), StandardCopyOption.REPLACE_EXISTING);
                    }
                    try (InputStream is = grader.getOutput().openInputStream()) {
                        Files.copy(is, Paths.get("out.txt"), StandardCopyOption.REPLACE_EXISTING);
                    }
                    System.out.println("Test found! Saved to in, out, answer txt.");
                    break;
                }
            }

            String yn = null;
            while (!Objects.equals(yn, "y") && !Objects.equals(yn, "n")) {
                System.out.print("Again? Y/N: ");
                yn = sc.nextLine().trim().toLowerCase();
            }
            if (Objects.equals(yn, "n")) break;
        }
    }
}
