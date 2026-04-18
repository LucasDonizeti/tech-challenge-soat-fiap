package com.techchallenge.oficina.infrastructure.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do Swagger/OpenAPI para documentação das APIs REST.
 * Conforme ADR-014: Implementação de Swagger para Documentação de APIs.
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Value("${spring.application.name:Tech Challenge Oficina}")
    private String applicationName;

    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " API")
                        .version(applicationVersion)
                        .description("API REST para sistema de gestão de oficina mecânica com múltiplos bounded contexts")
                        .contact(new Contact()
                                .name("Tech Challenge SOAT FIAP")
                                .email("techchallenge@fiap.com.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Autenticação JWT Bearer Token. Obtenha o token em /v1/auth/login")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .tags(List.of(
                        createTag("Autenticação", "Endpoints de autenticação e geração de tokens JWT"),
                        createTag("Cliente", "Endpoints de gestão de clientes"),
                        createTag("Ordem de Serviço", "Endpoints de gestão de ordens de serviço"),
                        createTag("Administrativo", "Endpoints administrativos do sistema"),
                        createTag("Saúde", "Endpoints de verificação de saúde do sistema")
                ));
    }

    private Tag createTag(String name, String description) {
        return new Tag()
                .name(name)
                .description(description);
    }
}
