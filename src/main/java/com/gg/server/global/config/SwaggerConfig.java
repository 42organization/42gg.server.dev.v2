package com.gg.server.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi group1() {
        return GroupedOpenApi.builder()
                .group("pingpong")
                .pathsToMatch("/pingpong/**")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        String jwtSchemeName = "jwtAuth";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("Bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .info(new Info().title("42GG V2 API")
                        .description("42GG 백엔드 프로젝트 API 명세서입니다.")
                        .version("v2.0.0"))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
