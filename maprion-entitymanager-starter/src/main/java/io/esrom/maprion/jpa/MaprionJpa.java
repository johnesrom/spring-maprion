package io.esrom.maprion.jpa;

import io.esrom.maprion.core.DefaultConverters;
import io.esrom.maprion.core.MaprionMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;

import java.util.List;
import java.util.Map;

/**
 * Adaptador entre o EntityManager e o MaprionMapper.
 * Permite mapear consultas nativas SQL (Tuple) para DTOs fortemente tipados,
 * com suporte a parâmetros posicionais, nomeados e paginação.
 *
 * Autor: John Esrom
 * Projeto: Maprion (io.esrom.maprion)
 */
public class MaprionJpa {

    private final MaprionMapper mapper;

    public MaprionJpa() {
        this.mapper = new MaprionMapper(DefaultConverters.defaults());
    }

    public MaprionJpa(MaprionMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 🔹 Versão 1 — Parâmetros posicionais (?1, ?2, ...)
     */
    public <T> List<T> map(EntityManager em, String sql, Class<T> targetType, Object... params) {
        Query q = em.createNativeQuery(sql, Tuple.class);
        for (int i = 0; i < params.length; i++) {
            q.setParameter(i + 1, params[i]);
        }
        @SuppressWarnings("unchecked")
        List<Tuple> tuples = q.getResultList();
        return mapper.map(new TupleRowSource(tuples), targetType);
    }

    /**
     * 🔹 Versão 2 — Parâmetros nomeados (:paramName)
     * Ideal para consultas com filtros dinâmicos e Map<String, Object>
     */
    public <T> List<T> map(EntityManager em, String sql, Class<T> targetType, Map<String, Object> namedParams) {
        Query q = em.createNativeQuery(sql, Tuple.class);

        if (namedParams != null && !namedParams.isEmpty()) {
            namedParams.forEach(q::setParameter);
        }

        @SuppressWarnings("unchecked")
        List<Tuple> tuples = q.getResultList();
        return mapper.map(new TupleRowSource(tuples), targetType);
    }

    /**
     * 🔹 Versão 3 — Parâmetros nomeados + paginação (limit, offset)
     * Perfeito para endpoints com Pageable.
     */
    public <T> List<T> map(EntityManager em, String sql, Class<T> targetType,
                           Map<String, Object> namedParams, int limit, int offset) {
        Query q = em.createNativeQuery(sql, Tuple.class);

        if (namedParams != null && !namedParams.isEmpty()) {
            namedParams.forEach(q::setParameter);
        }

        q.setMaxResults(limit);
        q.setFirstResult(offset);

        @SuppressWarnings("unchecked")
        List<Tuple> tuples = q.getResultList();
        return mapper.map(new TupleRowSource(tuples), targetType);
    }
}
