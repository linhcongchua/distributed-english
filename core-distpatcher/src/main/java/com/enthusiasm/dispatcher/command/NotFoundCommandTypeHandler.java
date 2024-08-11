package com.enthusiasm.dispatcher.command;

public class NotFoundCommandTypeHandler extends RuntimeException {
    public NotFoundCommandTypeHandler(String commandType) {
        super("Not found command type: " + commandType);
    }
}
