package ru.desnitsa.backend.configs;

import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Этот класс нужен, чтобы в Swagger UI отображался атрибут "Nullable" у необязательных параметров в схеме JSON объектов
 * */

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenApiCustomiser nullableOpenApiCustomizer() {
        return openApi -> openApi.getComponents().getSchemas().values().forEach(
                schema -> {
                    if (schema.getProperties() == null) {
                        return;
                    }
                    schema.getProperties().forEach(
                            (name, value) -> {
                                if (schema.getRequired() == null || !schema.getRequired().contains(name)) {
                                    ((Schema<Object>) value).setNullable(true);
                                }
                            }
                    );
                }
        );
    }
}
