package com.enthusiasm.dispatcher.event;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface EventDispatcher {
    String topic();
    String group();

    boolean isThreadPerPartition() default true;
}
