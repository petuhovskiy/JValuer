package com.petukhovsky.jvaluer.cli;

import com.petukhovsky.jvaluer.Language;
import com.petukhovsky.jvaluer.compiler.CompilationResult;
import com.petukhovsky.jvaluer.run.RunOptions;

import java.nio.file.Files;
import java.nio.file.Path;

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
        Path tests = cli.findPath(args[1]);
        if (exe == null || tests == null || !Files.isDirectory(tests) || Files.notExists(exe)) {
            cli.print("wrong paths");
            return;
        }

        RunOptions runOptions = new RunOptions();

        Language compileLanguage = null;
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
                        compileLanguage = tmp;
                        cli.print("autodetected language = " + tmp.getName() + CLI.ln);
                    } else cli.print("autodetection failed" + CLI.ln);
                    continue;
                }
                Language tmp = Language.findByName(lang);
                if (tmp == null) cli.print("unknown name: " + lang + CLI.ln);
                else {
                    cli.print("detected " + tmp.getName() + " language");
                    compileLanguage = tmp;
                }
                continue;
            }

            if (arg.startsWith("tl=")) {
                runOptions = runOptions.append("time_limit", arg.substring("tl=".length()));
                continue;
            }

            if (arg.startsWith("ml=")) {
                runOptions = runOptions.append("memory_limit", arg.substring("ml=".length()));
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
        if (needsCompile && compileLanguage == null) {
            cli.print("something went wrong");
            return;
        }
        if (needsCompile) {
            cli.print("Compiling..." + CLI.ln);
            CompilationResult result = compileLanguage.compiler().compile(exe);
            cli.print(result + CLI.ln);
            if (!result.isSuccess()) return;
            exe = result.getExe();
        }

    }

    @Override
    public void displayHelp(CLI cli) {
        cli.print("Usage: check {source|exe} {tests} args.." + CLI.ln +
                "Args examples: check a.cpp tests compile={auto|lang} ml=8M tl=5s");
    }
}
