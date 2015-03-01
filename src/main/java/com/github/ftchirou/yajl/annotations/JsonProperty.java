package com.github.ftchirou.yajl.annotations;

import com.github.ftchirou.yajl.serializer.JsonSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonProperty {
    String name() default "";
    Class<? extends JsonSerializer> serializeWith() default JsonSerializer.class;
}
