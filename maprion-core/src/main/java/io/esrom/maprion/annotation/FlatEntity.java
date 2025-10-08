package io.esrom.maprion.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FlatEntity {
    String prefix();
    NamingStrategy naming() default NamingStrategy.SNAKE_TO_CAMEL;
}
