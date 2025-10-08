package io.esrom.maprion.jpa;

import io.esrom.maprion.core.RowContext;
import io.esrom.maprion.core.RowSource;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;

import java.util.*;

public class TupleRowSource implements RowSource {

    private final List<RowContext> rows;

    public TupleRowSource(List<Tuple> tuples) {
        this.rows = new ArrayList<>(tuples.size());
        for (Tuple t : tuples) {
            Map<String,Object> m = new LinkedHashMap<>();
            for (TupleElement<?> e : t.getElements()) {
                String alias = e.getAlias();
                if (alias == null || alias.isBlank()) continue;
                m.put(alias.toLowerCase(Locale.ROOT), t.get(e));
            }
            rows.add(new MapRowContext(m));
        }
    }

    @Override
    public Iterator<RowContext> iterator() { return rows.iterator(); }

    static class MapRowContext implements RowContext {
        private final Map<String,Object> data;
        MapRowContext(Map<String,Object> data){ this.data = data; }
        @Override public Object get(String columnLabel){ return data.get(columnLabel); }
        @Override public Set<String> columns(){ return data.keySet(); }
    }
}
