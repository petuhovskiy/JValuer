package com.petukhovsky.jvaluer.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by petuh on 2/23/2016.
 */
public class Move implements CommandExecutor {

    private static Logger logger = Logger.getLogger(Move.class.getName());

    @Override
    public void execute(CLI cli, String[] args) {
        if (args.length < 2) {
            cli.print("missing args");
            return;
        }
        Path from = cli.findPath(args[0]);
        Path to = cli.findPath(args[1]);
        if (from == null || to == null || Files.notExists(from)) {
            cli.print("file not found");
            return;
        }
        try {
            Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Move failed", e);
            cli.print("move failed");
            return;
        }
        cli.print("moved successfully");
    }

    @Override
    public void displayHelp(CLI cli) {
        cli.print("Move files from/to storage" + CLI.ln + "Usage: {from} {to}");
    }
}
