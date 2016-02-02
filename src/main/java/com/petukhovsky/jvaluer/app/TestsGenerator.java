package com.petukhovsky.jvaluer.app;

import com.petukhovsky.jvaluer.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

/**
 * Created by Arthur on 12/25/2015.
 */
public class TestsGenerator {
    public static void main(String[] args) throws IOException {
        System.out.println("TestsGenerator v0.1. Testlib generator support.");
        Scanner sc = new Scanner(System.in);
        System.out.print("Generator source: ");
        String source = sc.nextLine().trim();
        System.out.print("Output folder: ");
        String folder = sc.nextLine().trim();
        System.out.print("Count: ");
        int count = sc.nextInt();
        sc.nextLine();
        System.out.print("Test pattern: ");
        String pattern = sc.nextLine().trim();
        Language language = Language.findByExtension(source.substring(source.lastIndexOf('.') + 1));
        CompilationResult result = language.compile(Paths.get(source));
        if (!result.isSuccess()) {
            System.out.println("Error while compiling");
            System.out.println(result.getComment());
            return;
        }
        System.out.println("Compiled successfully");
        Generator generator = new Generator(result.getExe());
        Path dir = Paths.get(folder);
        if (!Files.exists(dir)) Files.createDirectory(dir);
        for (int i = 1; i <= count; i++) {
            GeneratedData test = generator.generate(pattern.replaceAll("\\$", String.valueOf(i)));
            RunInfo info = test.getInfo();
            System.out.println("Test #" + i + " generated: " + info.getPassedTime() + "ms, exitcode " + info.getExitCode());
            Path out = dir.resolve(String.format("%02d.in", i));
            Files.copy(test.getPath(), out, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
