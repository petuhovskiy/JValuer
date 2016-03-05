package com.petukhovsky.jvaluer.cli;

import com.petukhovsky.jvaluer.Language;
import com.petukhovsky.jvaluer.checker.CheckResult;
import com.petukhovsky.jvaluer.checker.Checker;
import com.petukhovsky.jvaluer.checker.TokenChecker;
import com.petukhovsky.jvaluer.compiler.CompilationResult;
import com.petukhovsky.jvaluer.run.RunInfo;
import com.petukhovsky.jvaluer.run.RunVerdict;
import com.petukhovsky.jvaluer.run.Runner;
import com.petukhovsky.jvaluer.test.StringData;
import com.petukhovsky.jvaluer.util.ArgumentParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by petuh on 3/1/2016.
 */
public class Compare implements CommandExecutor {
    @Override
    public void execute(CLI cli, String[] args) {
        List<String> args0 = new ArrayList<>();
        List<String> args1 = new ArrayList<>();
        List<String> args2 = new ArrayList<>();
        List<String> args3 = new ArrayList<>();
        int it = 0;
        for (String arg : args) {
            if (arg.equals("|")) {
                if (++it > 3) break;
                continue;
            }
            switch (it) {
                case 0:
                    args0.add(arg);
                    break;
                case 1:
                    args1.add(arg);
                    break;
                case 2:
                    args2.add(arg);
                    break;
                case 3:
                    args3.add(arg);
                    break;
            }
        }

        Checker checker = new TokenChecker(); //TODO

        Map<String, String> map0 = ArgumentParser.parse(args0);
        Map<String, String> map1 = ArgumentParser.parse(args1);
        Map<String, String> map2 = ArgumentParser.parse(args2);

        String[] genArgs = new String[args3.size()];
        genArgs = args3.toArray(genArgs);

        boolean clear0 = false;
        boolean clear1 = false;
        boolean clear2 = false;

        Language lang0 = null;
        Language lang1 = null;
        Language lang2 = null;

        Path exe0 = cli.findPath(map0.get(""));
        Path exe1 = cli.findPath(map1.get(""));
        Path exe2 = cli.findPath(map2.get(""));

        if (map0.containsKey("compile")) {
            clear0 = true;
            lang0 = cli.findLanguage(map0.get("compile"), exe0);
            if (lang0 == null) return;
            cli.print("Compiling " + exe0 + CLI.ln);
            CompilationResult result = lang0.compiler().compile(exe0);
            cli.print(result + CLI.ln);
            if (!result.isSuccess()) return;
            exe0 = result.getExe();
        }

        if (map1.containsKey("compile")) {
            clear1 = true;
            lang1 = cli.findLanguage(map1.get("compile"), exe1);
            if (lang1 == null) return;
            cli.print("Compiling " + exe1 + CLI.ln);
            CompilationResult result = lang1.compiler().compile(exe1);
            cli.print(result + CLI.ln);
            if (!result.isSuccess()) return;
            exe1 = result.getExe();
        }

        if (map2.containsKey("compile")) {
            clear2 = true;
            lang2 = cli.findLanguage(map2.get("compile"), exe2);
            if (lang2 == null) return;
            cli.print("Compiling " + exe2 + CLI.ln);
            CompilationResult result = lang2.compiler().compile(exe2);
            cli.print(result + CLI.ln);
            if (!result.isSuccess()) return;
            exe2 = result.getExe();
        }

        try (Runner runner0 = new Runner();
             Runner runner1 = new Runner();
             Runner runner2 = new Runner()) {

            runner0.provideExecutable(exe0);
            runner1.provideExecutable(exe1);
            runner2.provideExecutable(exe2);

            if (clear0) Files.deleteIfExists(exe0);
            if (clear1) Files.deleteIfExists(exe1);
            if (clear2) Files.deleteIfExists(exe2);

            if (lang0 != null) runner0.setInvoker(lang0.invoker());
            if (lang1 != null) runner1.setInvoker(lang1.invoker());
            if (lang2 != null) runner2.setInvoker(lang2.invoker());

            runner0.setFiles(map0.get("in"), map0.get("out"));
            runner1.setFiles(map1.get("in"), map1.get("out"));
            runner2.setFiles(map2.get("in"), map2.get("out"));

            runner0.setLimits(map0.get("tl"), map0.get("ml"));
            runner1.setLimits(map1.get("tl"), map1.get("ml"));
            runner2.setLimits(map2.get("tl"), map2.get("ml"));

            boolean failed = false;
            int test = 1;

            while (!failed) {
                cli.print("Generating test #" + test + ": ");
                String[] generated = Arrays.copyOf(genArgs, genArgs.length);
                for (int i = 0; i < generated.length; i++) {
                    if (generated[i].equals("$test")) generated[i] = "" + test;
                }
                RunInfo info = runner2.run(new StringData(""), generated);
                cli.print(info + CLI.ln);

                info = runner0.run(runner2.getOutput());
                if (info.getRunVerdict() != RunVerdict.SUCCESS) failed = true;
                cli.print("Solution 1: " + info + CLI.ln);

                info = runner1.run(runner2.getOutput());
                if (info.getRunVerdict() != RunVerdict.SUCCESS) failed = true;
                cli.print("Solution 2: " + info + CLI.ln);

                CheckResult check = checker.check(runner2.getOutput(), runner0.getOutput(), runner1.getOutput());
                if (!check.isCorrect()) failed = true;
                cli.print("Checker: " + check + CLI.ln);

                test++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayHelp(CLI cli) {
        cli.print("Compares solutions" + CLI.ln +
                "Usage example: compare model.cpp compile=auto | test.exe | gen.cpp compile=auto | $test");
    }
}
