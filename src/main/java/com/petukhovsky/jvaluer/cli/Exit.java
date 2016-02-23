package com.petukhovsky.jvaluer.cli;

/**
 * Created by petuh on 2/23/2016.
 */
public class Exit implements CommandExecutor {
    @Override
    public void execute(CLI cli, String[] args) {
        cli.exit();
    }

    @Override
    public void displayHelp(CLI cli) {
        cli.print("Exit from jvaluer cli");
    }
}
