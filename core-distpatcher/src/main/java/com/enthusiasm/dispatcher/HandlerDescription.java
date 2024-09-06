package com.enthusiasm.dispatcher;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.Map;

@Data
public class HandlerDescription {
    private String topic; // todo: modify properties
    private String group;
    private boolean isThreadPerPartition;
    private Map<String, Method> methodHandler;
}
