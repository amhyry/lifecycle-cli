package com.application.cli.lifecyclecli.config;

import org.springframework.shell.CommandNotFound;
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;

public class CustomExceptionResolver implements CommandExceptionResolver {

    @Override
    public CommandHandlingResult resolve(Exception e) {
        if (e instanceof CommandNotFound ed) {
            ed.getMessage();
            return CommandHandlingResult.of("Hi, handled exception\n" + ed.getMessage(), 42);
        }
        return null;
    }
}
