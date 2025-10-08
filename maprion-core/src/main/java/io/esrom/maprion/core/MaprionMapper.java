package io.esrom.maprion.core;

import io.esrom.maprion.annotation.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Versão 1.0.3-core-recursive
 * - Independente de Spring
 * - Suporte recursivo ilimitado a objetos @Nested
 * - Log habilitado via System Property (-Dmaprion.log2f=true) ou construtor
 */
public class MaprionMapper {

    private final List<TypeConverter> converters;
    private final boolean log2f;

    // Construtor padrão: lê do System property
    public MaprionMapper(TypeConverter... converters) {
        this(Boolean.parseBoolean(System.getProperty("maprion.log2f", "false")), converters);
    }

    // Construtor explícito: usado pelo starter (injeta log2f via Environment)
    public MaprionMapper(boolean log2f, TypeConverter... converters) {
        this.converters = Arrays.asList(converters);
        this.log2f = log2f;
    }

    protected boolean log2f() {
        return log2f;
    }

    // -------------------------------------------------------------------------
    // Mapeamento principal
    // -------------------------------------------------------------------------
    public <T> List<T> map(RowSource source, Class<T> rootType) {
        FlatEntity fe = rootType.getAnnotation(FlatEntity.class);
        if (fe == null)
            throw new IllegalArgumentException("@FlatEntity missing on " + rootType);

        String rootPrefix = fe.prefix() + "_";
        Map<Object, T> identity = new LinkedHashMap<>();

        if (log2f())
            System.out.println("\n🔍 [Maprion] Iniciando mapeamento para " + rootType.getSimpleName()
                    + " (prefix: " + rootPrefix + ")");

        for (RowContext row : source) {
            Object rootId = row.get(rootPrefix + "uuid");
            if (rootId == null)
                rootId = row.get(rootPrefix + "id");
            if (rootId == null)
                continue;

            Object convertedRootId = convert(rootId, findIdFieldType(rootType));

            T root = identity.get(convertedRootId);
            if (root == null) {
                root = instantiate(rootType);
                fillFields(root, row, rootPrefix);
                processNested(root, row);
                processCollections(root, row);
                identity.put(convertedRootId, root);
            } else {
                fillFields(root, row, rootPrefix);
                processNested(root, row);
                processCollections(root, row);
            }
        }

        if (log2f())
            System.out.println("✅ [Maprion] Mapeamento finalizado. Total: " + identity.size() + " registros.");

        return new ArrayList<>(identity.values());
    }

    // -------------------------------------------------------------------------
    // Nested recursivo
    // -------------------------------------------------------------------------
    private void processNested(Object parent, RowContext row) {
        for (Field f : parent.getClass().getDeclaredFields()) {
            if (!f.isAnnotationPresent(Nested.class)) continue;

            Nested n = f.getAnnotation(Nested.class);
            Class<?> t = (n.type() == Void.class) ? f.getType() : n.type();
            String prefix = n.prefix() + "_";

            boolean hasAny = hasAnyColumn(row, prefix);
            if (!hasAny) continue;

            Object nested = instantiate(t);
            fillFields(nested, row, prefix);

            if (log2f())
                System.out.println("   ↳ [Maprion] Nested: " + parent.getClass().getSimpleName()
                        + "." + f.getName() + " → " + t.getSimpleName() + " (prefix: " + prefix + ")");

            // Processa subníveis recursivamente
            processNested(nested, row);
            set(parent, f, nested);
        }
    }

    // -------------------------------------------------------------------------
    // OneToMany
    // -------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private void processCollections(Object parent, RowContext row) {
        for (Field f : parent.getClass().getDeclaredFields()) {
            if (!f.isAnnotationPresent(OneToMany.class)) continue;

            OneToMany m = f.getAnnotation(OneToMany.class);
            String prefix = m.prefix() + "_";
            Object itemId = row.get(prefix + m.idColumn());
            if (itemId == null) continue;

            Object convertedItemId = convert(itemId, findIdFieldType(m.elementType()));
            Object item = instantiate(m.elementType());
            fillFields(item, row, prefix);
            processNested(item, row);

            List<Object> list = (List<Object>) get(parent, f);
            if (list == null) {
                list = new ArrayList<>();
                set(parent, f, list);
            }

            boolean exists = list.stream().anyMatch(it ->
                    Objects.equals(readId(it, m.idColumn()), convertedItemId));
            if (!exists) list.add(item);
        }
    }

    // -------------------------------------------------------------------------
    // Utilitários
    // -------------------------------------------------------------------------
    private boolean hasAnyColumn(RowContext row, String prefix) {
        for (String c : row.columns()) {
            if (c.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT))
                    && row.get(c) != null)
                return true;
        }
        return false;
    }

    private void fillFields(Object target, RowContext row, String prefix) {
        for (Field f : target.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(Nested.class) || f.isAnnotationPresent(OneToMany.class)) continue;

            String col = prefix + (f.isAnnotationPresent(Column.class)
                    ? f.getAnnotation(Column.class).name()
                    : f.getName());

            Object v = row.get(col.toLowerCase(Locale.ROOT));
            if (v == null) v = row.get(col);
            if (v == null) continue;

            Object cv = convert(v, f.getType());
            set(target, f, cv);

            if (log2f())
                System.out.println("      • " + target.getClass().getSimpleName()
                        + "." + f.getName() + " = " + cv);
        }
    }

    private Object convert(Object v, Class<?> targetType) {
        if (v == null) return null;
        if (targetType.isAssignableFrom(v.getClass())) return v;
        for (TypeConverter c : converters) {
            if (c.supports(targetType)) return c.convert(v, targetType);
        }
        return v;
    }

    private <T> T instantiate(Class<T> t) {
        try {
            return t.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate " + t, e);
        }
    }

    private static void set(Object target, Field f, Object value) {
        try {
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object get(Object target, Field f) {
        try {
            f.setAccessible(true);
            return f.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object readId(Object obj, String fallbackName) {
        for (Field f : obj.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(Id.class) || f.getName().equalsIgnoreCase(fallbackName)) {
                f.setAccessible(true);
                try {
                    return f.get(obj);
                } catch (Exception ignored) {}
            }
        }
        return null;
    }

    private Class<?> findIdFieldType(Class<?> type) {
        for (Field f : type.getDeclaredFields()) {
            if (f.isAnnotationPresent(Id.class)
                    || f.getName().equalsIgnoreCase("uuid")
                    || f.getName().equalsIgnoreCase("id")) {
                return f.getType();
            }
        }
        return Object.class;
    }
}
