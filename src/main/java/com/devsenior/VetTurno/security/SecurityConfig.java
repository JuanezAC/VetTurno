package com.devsenior.VetTurno.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * CONFIGURACIÓN TEMPORAL — Parte 3.
 * Todas las rutas quedan abiertas para poder probar el flujo REST
 * (aún no existe registro, login ni roles). Esta versión se REEMPLAZA
 * por completo en la Parte 5 con la política JWT, roles USER/ADMIN
 * y modo stateless.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
