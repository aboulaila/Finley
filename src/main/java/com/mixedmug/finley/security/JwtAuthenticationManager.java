package com.mixedmug.finley.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtUtil jwtUtil;
    private final FinleyUserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationManager(JwtUtil jwtUtil, FinleyUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        String email = jwtUtil.getEmailFromToken(token);

        return userDetailsService.findByUsername(email)
                .map(userDetails -> {
                    if (Boolean.TRUE.equals(jwtUtil.validateToken(token, userDetails.getUsername()))) {
                        return authentication;
                    } else {
                        throw new AuthenticationCredentialsNotFoundException("Invalid JWT token") {};
                    }
                });
    }
}