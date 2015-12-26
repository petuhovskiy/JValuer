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
        String inFile = "input.txt";
        String outFile = "output.txt";
        String exe = null;
        String folder = null;
        String time = null;
        String memory = null;
        boolean silent = false;
        boolean files = false;
        for (int i = 0; i < args.length; i++) {
            String key = args[i];
            switch (key) {
                case "-silent":
                    silent = true;
                    break;
                case "-in":
                    if (++i == args.length) {
                        System.err.println("In file isn't specified");
                        return;
                    }
                    inFile = args[i];
                    break;
                case "-out":
                    if (++i == args.length) {
                        System.err.println("Out file isn't specified");
                        return;
                    }
                    outFile = args[i];
                    break;
                case "-exe":
                    if (++i == args.length) {
                        System.err.println("Exe file isn't specified");
                        return;
                    }
                    exe = args[i];
                    break;
                case "-folder":
                    if (++i == args.length) {
                        System.err.println("Folder isn't specified");
                        return;
                    }
                    folder = args[i];
                    break;
                case "-time":
                    if (++i == args.length) {
                        System.err.println("Time isn't specified");
                        return;
                    }
                    time = args[i];
                    break;
                case "-memory":
                    if (++i == args.length) {
                        System.err.println("Memory isn't specified");
                        return;
                    }
                    memory = args[i];
                    break;
                case "-files":
                    files = true;
                    break;
                case "-debug":
                    Local.setDebug(true);
                    break;
                default:
                    System.err.println("Unknown parameter " + key);
                    return;
            }
        }

        Scanner sc = new Scanner(System.in);

        if (exe == null) {
            System.out.print("Executable filename: ");
            exe = sc.nextLine().trim();
        }
        if (folder == null) {
            System.out.print("Folder with tests: ");
            folder = sc.nextLine().trim();
        }

        if (!silent) {
            if (time == null) {
                System.out.print("Time limit: ");
                time = sc.nextLine().trim();
            }
            if (memory == null) {
                System.out.print("Memory limit: ");
                memory = sc.nextLine().trim();
            }
        }

        if (files) {
            System.out.print("Input: ");
            inFile = sc.nextLine().trim();
            System.out.print("Output: ");
            outFile = sc.nextLine().trim();
        }

        RunOptions options = new RunOptions("trusted", "");
        if (time != null && !time.isEmpty()) options = options.append("time_limit", time);
        if (memory != null && !memory.isEmpty()) options = options.append("memory_limit", memory);

        Runner runner = new Runner(inFile, outFile, options);
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
        System.out.println("Test " + name + " " + test);
    }
}
