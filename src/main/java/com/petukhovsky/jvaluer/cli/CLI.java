package com.petukhovsky.jvaluer.cli;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by petuh on 2/19/2016.
 */
public class CLI {

    private Scanner sc;
    private PrintStream pw;

    private CLI() {
        sc = new Scanner(System.in);
        pw = System.out;
    }

    public static void main(String[] args) {
        new CLI().start();
    }

    private void start() {
        while (true) {
            pw.print("jvaluer>");
            String command = sc.nextLine().trim();
            if (command.isEmpty()) continue;
            String arr[] = command.split("\\s+");
            System.out.println(Arrays.toString(arr));
        }
    }
}
