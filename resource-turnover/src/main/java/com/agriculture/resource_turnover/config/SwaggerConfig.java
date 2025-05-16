package com.agriculture.resource_turnover.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("oauth2", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new io.swagger.v3.oas.models.security.OAuthFlows()
                                        .authorizationCode(new io.swagger.v3.oas.models.security.OAuthFlow()
                                                .authorizationUrl("http://localhost:8181/realms/agriculture-realm/protocol/openid-connect/auth")
                                                .tokenUrl("http://localhost:8181/realms/agriculture-realm/protocol/openid-connect/token")
                                                .scopes(new io.swagger.v3.oas.models.security.Scopes()
                                                        .addString("openid", "OpenID Connect")
                                                )
                                        )
                                )
                        )
                )
                .info(new Info()
                        .title("Agriculture API")
                        .version("1.0")
                        .description("API для управління аграрними ресурсами."))
                .addSecurityItem(new SecurityRequirement().addList("oauth2"));
    }
}