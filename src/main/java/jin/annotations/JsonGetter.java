package jin.annotations;

import jin.databind.JsonSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonGetter {
    String name() default "";
    Class<? extends JsonSerializer> serializeWith() default JsonSerializer.class;
}
