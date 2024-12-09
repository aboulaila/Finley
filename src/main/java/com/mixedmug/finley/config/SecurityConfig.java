package com.mixedmug.finley.config;

import com.mixedmug.finley.security.JwtAuthenticationManager;
import com.mixedmug.finley.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import reactor.core.publisher.Mono;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationManager jwtAuthenticationManager, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationManager = jwtAuthenticationManager;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, CorsConfigurationSource corsConfigurationSource) {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .pathMatchers("/api/auth/**", "/oauth2/**").permitAll()
                        .pathMatchers("/api/openai/**", "/api/finley/**").authenticated()
                        .anyExchange().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {
                            // Handle OAuth2 success
                            return Mono.empty();
                        })
                        .authenticationFailureHandler((webFilterExchange, exception) -> {
                            // Handle OAuth2 failure
                            return Mono.empty();
                        })
                )
                .build();
    }
}