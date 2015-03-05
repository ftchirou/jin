package jin.annotations;

import jin.databind.JsonDeserializer;
import jin.databind.JsonSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Json {
    String propertyName() default "";
    boolean ignore() default false;
    Class<? extends JsonSerializer> serializeWith() default JsonSerializer.class;
    Class<? extends JsonDeserializer> deserializeWith() default JsonDeserializer.class;
}
