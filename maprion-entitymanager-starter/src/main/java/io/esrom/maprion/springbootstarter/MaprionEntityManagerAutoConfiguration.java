package io.esrom.maprion.springbootstarter;

import io.esrom.maprion.core.DefaultConverters;
import io.esrom.maprion.core.MaprionMapper;
import io.esrom.maprion.jpa.MaprionJpa;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@AutoConfiguration
@ConditionalOnClass(EntityManager.class)
public class MaprionEntityManagerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MaprionMapper maprionMapper(Environment env) {
        // Lê o valor de maprion.log2f direto do application.properties
        boolean log2f = Boolean.parseBoolean(env.getProperty("maprion.log2f", "false"));
        return new MaprionMapper(log2f, DefaultConverters.defaults());
    }

    @Bean
    @ConditionalOnMissingBean
    public MaprionJpa maprionJpa(MaprionMapper mapper) {
        return new MaprionJpa(mapper);
    }
}
