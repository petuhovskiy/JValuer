package com.petukhovsky.jvaluer.cli;

import com.petukhovsky.jvaluer.Language;
import com.petukhovsky.jvaluer.checker.Checker;
import com.petukhovsky.jvaluer.checker.TokenChecker;
import com.petukhovsky.jvaluer.compiler.CompilationResult;
import com.petukhovsky.jvaluer.run.RunInfo;
import com.petukhovsky.jvaluer.run.Runner;
import com.petukhovsky.jvaluer.task.Test;
import com.petukhovsky.jvaluer.task.Tests;
import com.petukhovsky.jvaluer.test.TestVerdict;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Created by petuh on 2/23/2016.
 */
public class Check implements CommandExecutor {
    @Override
    public void execute(CLI cli, String[] args) {
        if (args.length < 2) {
            cli.print("few arguments");
            return;
        }
        Path exe = cli.findPath(args[0]);
        Path dir = cli.findPath(args[1]);
        if (exe == null || dir == null || !Files.isDirectory(dir) || Files.notExists(exe)) {
            cli.print("wrong paths");
            return;
        }

        String timeLimit = null;
        String memoryLimit = null;

        Checker checker = new TokenChecker(); //TODO

        Language language = null;
        boolean needsCompile = false;

        String input = "stdin";
        String output = "stdout";

        for (int i = 2; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("compile=")) {
                needsCompile = true;
                String lang = arg.substring("compile=".length());
                if (lang.equals("auto")) {
                    Language tmp = Language.findByPath(exe);
                    if (tmp != null) {
                        language = tmp;
                        cli.print("autodetected language = " + tmp.getName() + CLI.ln);
                    } else cli.print("autodetection failed" + CLI.ln);
                    continue;
                }
                Language tmp = Language.findByName(lang);
                if (tmp == null) cli.print("unknown name: " + lang + CLI.ln);
                else {
                    cli.print("detected " + tmp.getName() + " language");
                    language = tmp;
                }
                continue;
            }

            if (arg.startsWith("tl=")) {
                timeLimit = arg.substring("tl=".length());
                continue;
            }

            if (arg.startsWith("ml=")) {
                memoryLimit = arg.substring("ml=".length());
                continue;
            }

            if (arg.startsWith("in=")) {
                input = arg.substring("in=".length());
                continue;
            }

            if (arg.startsWith("out=")) {
                output = arg.substring("out=".length());
                continue;
            }
        }
        if (needsCompile && language == null) {
            cli.print("something went wrong");
            return;
        }
        if (needsCompile) {
            cli.print("Compiling..." + CLI.ln);
            CompilationResult result = language.compiler().compile(exe);
            cli.print(result + CLI.ln);
            if (!result.isSuccess()) return;
            exe = result.getExe();
        }
        Test[] tests;
        try {
            tests = Tests.importFromFolder(dir);
        } catch (IOException e) {
            cli.print("problem while importing tests");
            return;
        }
        Arrays.sort(tests, (test1, test2) -> {
            String name1 = test1.getTestName();
            String name2 = test2.getTestName();
            for (int i = 0; i < Math.max(name1.length(), name2.length()); i++) {
                boolean isDigit1 = i < name1.length() && Character.isDigit(name1.charAt(i));
                boolean isDigit2 = i < name2.length() && Character.isDigit(name2.charAt(i));
                if (isDigit1 && isDigit2) continue;
                if (isDigit1) return 1;
                if (isDigit2) return -1;
                if (i == Math.min(name1.length(), name2.length())) break;
                int comp = Character.compare(name1.charAt(i), name2.charAt(i));
                if (comp != 0) return comp;
            }
            return name1.compareTo(name2);
        });
        try (Runner runner = new Runner()) {
            runner.setFiles(input, output);
            runner.setLimits(timeLimit, memoryLimit);
            if (language != null) runner.setInvoker(language.invoker());
            runner.provideExecutable(exe);
            if (needsCompile) Files.deleteIfExists(exe);
            int count = 0;
            int accepted = 0;
            for (Test test : tests) {
                RunInfo info = runner.run(test.getIn());
                TestVerdict verdict = new TestVerdict(test.getIn(), test.getOut(), runner.getOutput(), info, checker);
                cli.print(test.getTestName() + " - " + verdict + CLI.ln);
                count++;
                if (verdict.isAccepted()) accepted++;
            }
            cli.print("Testing complete. Tests passed: " + accepted + "/" + count + CLI.ln);
        } catch (IOException e) {
            e.printStackTrace();
            cli.print("failed to create runner");
        }
    }

    @Override
    public void displayHelp(CLI cli) {
        cli.print("Usage: check {source|exe} {tests} args.." + CLI.ln +
                "Args examples: check a.cpp tests compile={auto|lang} ml=8M tl=5s");
    }
}
