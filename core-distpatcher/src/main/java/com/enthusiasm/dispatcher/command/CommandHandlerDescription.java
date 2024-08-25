package com.enthusiasm.dispatcher.command;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.Map;

@Data
public class CommandHandlerDescription {
    private String topic; // todo: modify properties
    private String group;
    private boolean isThreadPerPartition;
    private Map<String, Method> methodHandler;
}
