package com.mixedmug.finley.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                // Disable CSRF as it's a stateless API
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // Authorize requests
                .authorizeExchange(auth -> auth
                        // Permit Swagger UI and API docs
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Secure OpenAI endpoints
                        .pathMatchers("/api/openai/**").authenticated()

                        // Allow other requests
                        .anyExchange().permitAll()
                )

                // Use HTTP Basic authentication
                .httpBasic(Customizer.withDefaults())

                // Optionally, use form-based authentication
                // .formLogin(Customizer.withDefaults())

                // Disable default logout
                .logout(ServerHttpSecurity.LogoutSpec::disable);

        return http.build();
    }
}