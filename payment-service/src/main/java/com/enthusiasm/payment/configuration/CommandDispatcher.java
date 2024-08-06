package com.enthusiasm.payment.configuration;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface CommandDispatcher {
    String aggregate();
    String service();

    boolean isThreadPerPartition() default true;
}
