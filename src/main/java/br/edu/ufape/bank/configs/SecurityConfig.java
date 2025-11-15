package br.edu.ufape.bank.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desabilitar CSRF (comum para APIs stateless)
            .csrf(csrf -> csrf.disable())

            // 2. API será STATELESS (não usará sessões HTTP)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 3. Configurar regras de autorização
            .authorizeHttpRequests(auth -> auth
                // Você precisa de um endpoint PÚBLICO para o Keycloak
                // O Keycloak não gerencia o cadastro na SUA tabela,
                // apenas a autenticação. Veremos isso no Passo 4.
                // Por enquanto, vamos proteger tudo:
                .anyRequest().authenticated() // <-- TODAS as requisições exigem um token
            )

            // 4. Configurar o Spring para atuar como Resource Server
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                // Aqui você pode adicionar customizações do JWT, se necessário
            }));

        return http.build();
    }
}
