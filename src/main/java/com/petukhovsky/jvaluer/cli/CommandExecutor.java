package com.petukhovsky.jvaluer.cli;

/**
 * Created by petuh on 2/23/2016.
 */
public interface CommandExecutor {
    void execute(CLI cli, String[] args);

    void displayHelp(CLI cli);
}
