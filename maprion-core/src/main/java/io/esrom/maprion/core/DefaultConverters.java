package io.esrom.maprion.core;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.*;
import java.util.UUID;

public final class DefaultConverters {

    public static TypeConverter[] defaults() {
        return new TypeConverter[] {
                new Simple(UUID.class) {
                    @Override public Object convert(Object v, Class<?> t) { return UUID.fromString(v.toString()); }
                },
                new Simple(BigDecimal.class) {
                    @Override public Object convert(Object v, Class<?> t) { return new BigDecimal(v.toString()); }
                },
                new Simple(Boolean.class) {
                    @Override public Object convert(Object v, Class<?> t) {
                        if (v instanceof Boolean b) return b;
                        String s = String.valueOf(v).toLowerCase();
                        return "1".equals(s) || "true".equals(s) || "t".equals(s) || "y".equals(s);
                    }
                },
                new Simple(Instant.class) {
                    @Override public Object convert(Object v, Class<?> t) {
                        if (v instanceof Timestamp ts) return ts.toInstant();
                        if (v instanceof LocalDateTime ldt) return ldt.toInstant(ZoneOffset.UTC);
                        return Instant.parse(v.toString());
                    }
                },
                new Simple(LocalDate.class) {
                    @Override public Object convert(Object v, Class<?> t) {
                        if (v instanceof LocalDate d) return d;
                        if (v instanceof Timestamp ts) return ts.toLocalDateTime().toLocalDate();
                        if (v instanceof LocalDateTime dt) return dt.toLocalDate();
                        return LocalDate.parse(v.toString());
                    }
                },
                new Simple(LocalDateTime.class) {
                    @Override public Object convert(Object v, Class<?> t) {
                        if (v instanceof LocalDateTime d) return d;
                        if (v instanceof Timestamp ts) return ts.toLocalDateTime();
                        if (v instanceof Instant i) return LocalDateTime.ofInstant(i, ZoneOffset.UTC);
                        return LocalDateTime.parse(v.toString());
                    }
                }
        };
    }

    private static abstract class Simple implements TypeConverter {
        private final Class<?> type;
        Simple(Class<?> type) { this.type = type; }

        @Override
        public boolean supports(Class<?> targetType) {
            return type.equals(targetType)
                    || (targetType.isPrimitive() && wrap(targetType).equals(type));
        }

        static Class<?> wrap(Class<?> p) {
            if (p == boolean.class) return Boolean.class;
            if (p == int.class) return Integer.class;
            if (p == long.class) return Long.class;
            if (p == double.class) return Double.class;
            if (p == float.class) return Float.class;
            if (p == short.class) return Short.class;
            if (p == byte.class) return Byte.class;
            if (p == char.class) return Character.class;
            return p;
        }
    }
}
