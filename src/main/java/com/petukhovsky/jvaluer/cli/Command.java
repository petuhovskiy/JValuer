package com.petukhovsky.jvaluer.cli;

/**
 * Created by petuh on 2/23/2016.
 */
public enum Command {
    EXIT("exit", new Exit()),
    COMPILE("compile", new Compile()),
    MOVE("move", new Move()),
    COPY("copy", new Copy()),
    CHECK("check", new Check());

    private String name;
    private CommandExecutor executor;

    Command(String name, CommandExecutor executor) {
        this.name = name;
        this.executor = executor;
    }

    public static CommandExecutor findCommand(String name) {
        for (Command command : Command.values())
            if (command.name.equals(name)) return command.executor;
        return null;
    }
}
