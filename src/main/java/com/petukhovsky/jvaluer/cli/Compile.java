package com.petukhovsky.jvaluer.cli;

import com.petukhovsky.jvaluer.Language;
import com.petukhovsky.jvaluer.Local;
import com.petukhovsky.jvaluer.compiler.CompilationResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by petuh on 2/23/2016.
 */
public class Compile implements CommandExecutor {

    private static Path prevSource;

    @Override
    public void execute(CLI cli, String[] args) {
        if (args.length == 0) {
            displayHelp(cli);
            return;
        }
        if (args.length < 1) {
            cli.print("source is missing");
            return;
        }
        if (args[0].equals("!") && prevSource == null) {
            cli.print("previous source is missing");
            return;
        }
        Path source = args[0].equals("!") ? prevSource : cli.findPath(args[0]);
        if (source == null || Files.notExists(source)) {
            cli.print("source file doesn't exist");
            return;
        }
        prevSource = source;
        if (args.length < 2) {
            cli.print("destination is missing");
            return;
        }
        Path exe = args[1].equals("!") ? resolveExecutable(source) : cli.findPath(args[1]);
        Language language = null;
        List<String> list = new ArrayList<>();
        for (int i = 2; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("language=")) {
                Language tmp = Language.findByName(arg.substring("language=".length()));
                if (tmp != null) language = tmp;
                continue;
            }
            if (arg.startsWith("-D")) {
                list.add(arg.substring("-D".length()));
            }
        }
        if (language == null) {
            Language tmp = Language.findByPath(source);
            if (tmp == null) {
                cli.print("couldn't detect language");
                return;
            }
            language = tmp;
            cli.print("auto-detected language is " + tmp.getName());
        }
        String defines[] = new String[list.size()];
        defines = list.toArray(defines);
        CompilationResult result = language.compiler().compile(exe, source, defines);
        cli.print(CLI.ln + result);
    }

    @Override
    public void displayHelp(CLI cli) {
        cli.print("Compile solution");
    }

    private Path resolveExecutable(Path source) {
        String filename = source.getFileName().toString();
        int pos = filename.lastIndexOf('.');
        if (pos != -1) filename = filename.substring(0, pos);
        filename += Local.getExecutableSuffix();
        return source.getParent().resolve(filename);
    }
}
