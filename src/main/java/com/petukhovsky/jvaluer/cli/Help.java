package com.petukhovsky.jvaluer.cli;

/**
 * Created by petuh on 2/29/2016.
 */
public class Help implements CommandExecutor {
    @Override
    public void execute(CLI cli, String[] args) {
        if (args.length == 0) {
            displayHelp(cli);
            return;
        }
        String command = args[0];
        CommandExecutor commandExecutor = Command.findCommand(command);
        if (commandExecutor == null) {
            cli.print("Command \"" + command + "\" not found");
            return;
        }
        commandExecutor.displayHelp(cli);
    }

    @Override
    public void displayHelp(CLI cli) {
        cli.print("Display help" + CLI.ln);
    }
}
