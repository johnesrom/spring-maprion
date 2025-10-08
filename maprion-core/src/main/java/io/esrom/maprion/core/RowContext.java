package io.esrom.maprion.core;

import java.util.Set;

public interface RowContext {
    Object get(String columnLabel);
    Set<String> columns();
}
