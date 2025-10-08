package io.esrom.maprion.spring;

import io.esrom.maprion.core.MaprionMapper;
import io.esrom.maprion.core.TypeConverter;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

/**
 * Extensão do MaprionMapper que integra com Spring Boot Environment.
 */
public class MaprionSpringMapper extends MaprionMapper {

    private final Environment env;

    public MaprionSpringMapper(Environment env, TypeConverter... converters) {
        super(false, converters); // desativa o SystemProperty
        this.env = env;
    }

    @Override
    protected boolean log2f() {
        return Boolean.parseBoolean(env.getProperty("maprion.log2f", "false"));
    }
}
