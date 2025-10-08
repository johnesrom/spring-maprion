package io.esrom.maprion.core;

public interface TypeConverter {
    boolean supports(Class<?> targetType);
    Object convert(Object value, Class<?> targetType);
}
