package com.petukhovsky.jvaluer.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by petuh on 2/19/2016.
 */
public class CLI {

    public static String ln = System.lineSeparator();
    private static Logger logger = Logger.getLogger(CLI.class.getName());
    private Scanner sc;
    private PrintStream pw;
    private Path storage;
    private boolean isStarted = false;

    private CLI() {
        sc = new Scanner(System.in);
        pw = System.out;
        print("JValuer CLI. Use help to see avaliable commands" + ln);
        this.setStorage("jvaluer-storage/");
    }

    public static void main(String[] args) {
        Logger log = LogManager.getLogManager().getLogger("");
        for (Handler h : log.getHandlers()) {
            h.setLevel(Level.WARNING);
        }
        new CLI().start();
    }

    private void setStorage(String path) {
        storage = Paths.get(path);
        if (!Files.exists(storage) || !Files.isDirectory(storage)) {
            try {
                Files.deleteIfExists(storage);
                Files.createDirectory(storage);
                print("New storage created => " + storage.toAbsolutePath());
            } catch (IOException e) {
                logger.log(Level.WARNING, "storage creating exception", e);
            }
        } else {
            print("Storage already exists => " + storage.toAbsolutePath());
        }
    }

    public void print(String message) {
        pw.print(message);
    }

    public String readLine() {
        return sc.nextLine().trim();
    }

    public String[] readTokens() {
        return readLine().split("\\s+");
    }

    private void start() {
        isStarted = true;
        while (isStarted) {
            pw.print(ln + "jvaluer> ");
            String arr[] = readTokens();
            if (arr.length == 0) {
                print("");
                continue;
            }
            CommandExecutor executor = Command.findCommand(arr[0]);
            if (executor == null) {
                print("Command \"" + arr[0] + "\" not found");
                continue;
            }
            String args[] = new String[arr.length - 1];
            System.arraycopy(arr, 1, args, 0, arr.length - 1);
            executor.execute(this, args);
        }
    }

    public void exit() {
        isStarted = false;
    }

    public Path findPath(String path) {
        if (path.startsWith("$")) return storage.resolve(path.substring(1));
        return Paths.get(path);
    }
}