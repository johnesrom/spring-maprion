package io.esrom.maprion.autoconfigure;

import io.esrom.maprion.core.DefaultConverters;
import io.esrom.maprion.spring.MaprionSpringMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MaprionAutoConfiguration {

    @Bean
    public MaprionSpringMapper maprionMapper(Environment env) {
        return new MaprionSpringMapper(env, DefaultConverters.defaults());
    }
}
