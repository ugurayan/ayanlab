package com.ayanlab.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_AUTH_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${spring.application.name:ayanlabs-api}") String applicationName) {
        String title = applicationName;
        return new OpenAPI()
            .info(new Info()
                .title(title)
                .version("1.0.0")
                .description("AyanLab REST API — " + title)
                .contact(new Contact().name("AyanLabs Team").email("devops@ayanlabs.example"))
                .license(new License().name("Apache 2.0").url("https://springdoc.org"))
            )
            .components(new Components()
                .addSecuritySchemes(BEARER_AUTH_SCHEME_NAME,
                    new SecurityScheme()
                        .name(BEARER_AUTH_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description(
                            "JWT from login/register (`accessToken`). Paste the token only; Swagger sends "
                                + "`Authorization: Bearer <token>`.")
                )
            )
            .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH_SCHEME_NAME));
    }
}