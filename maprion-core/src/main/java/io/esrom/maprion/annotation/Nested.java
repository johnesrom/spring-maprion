package io.esrom.maprion.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Nested {
    String prefix();
    Class<?> type() default Void.class;
}
