package com.github.ftchirou.yajl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonTypeInfo {
    Id use() default Id.CLASS;
    String property() default "@class";

    public enum Id {
        CLASS,
        CUSTOM
    }
}
