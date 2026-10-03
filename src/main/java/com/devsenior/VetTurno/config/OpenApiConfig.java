package com.devsenior.VetTurno.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Identidad de la API en OpenAPI/Swagger — Parte 7.
 * Describe VetTurno y expone el esquema Bearer JWT para que el
 * botón Authorize de Swagger UI agregue el token a las peticiones.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI vetTurnoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VetTurno")
                        .description("API REST de la Veterinaria Huellitas: "
                                + "gestión de propietarios, mascotas, veterinarios "
                                + "y agenda de citas.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("VetTurno - Taller evaluativo Módulo 3")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token que devuelve POST /api/auth/login. "
                                        + "Se puede pegar con o sin el prefijo Bearer.")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
