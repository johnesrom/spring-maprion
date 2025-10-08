package io.esrom.maprion.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OneToMany {
    String prefix();
    Class<?> elementType();
    String idColumn() default "uuid";
}
