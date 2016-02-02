package com.petukhovsky.jvaluer.app;

import com.petukhovsky.jvaluer.PathData;
import com.petukhovsky.jvaluer.RunInfo;
import com.petukhovsky.jvaluer.RunOptions;
import com.petukhovsky.jvaluer.Runner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

/**
 * Created by Arthur on 12/18/2015.
 */
public class TestsSolver {
    public static void main(String[] args) throws IOException {
        System.out.println("TestsSolver v0.1. Notice that input files must be in *.in format");
        Scanner sc = new Scanner(System.in);
        System.out.print("Executable: ");
        String exe = sc.nextLine().trim();
        System.out.print("Folder: ");
        String folder = sc.nextLine().trim();
        System.out.print("Input: ");
        String input = sc.nextLine().trim();
        if (input.isEmpty()) input = "stdin";
        System.out.print("Output: ");
        String output = sc.nextLine().trim();
        if (output.isEmpty()) output = "stdout";

        RunOptions options = new RunOptions("trusted", "");

        Runner runner = new Runner(input, output, options);
        runner.provideExecutable(Paths.get(exe));

        Path tests = Paths.get(folder);
        Files.list(tests).forEach(path -> {
            String name = path.getFileName().toString();
            if (name.endsWith(".in")) {
                name = name.substring(0, name.length() - 3);
                Path out = path.getParent().resolve(name + ".out");
                RunInfo info = runner.run(new PathData(path));
                System.out.println("Test " + name + " generated. " + info.getUserTime() + " " + info.getComment());
                try (InputStream is = runner.getOutput().openInputStream()) {
                    Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
