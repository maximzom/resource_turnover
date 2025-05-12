package com.agriculture.resource_turnover.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .info(new Info()
                        .title("Agriculture API")
                        .version("1.0")
                        .description("Документація API для системи управління замовленнями"));
    }
}