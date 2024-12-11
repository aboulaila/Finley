package com.mixedmug.finley.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mixedmug.finley.model.User;
import com.mixedmug.finley.repository.UserRepository;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OAuth2LoginSuccessHandler implements ServerAuthenticationSuccessHandler {
    public static final String EMAIL = "email";
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public OAuth2LoginSuccessHandler(UserRepository userRepository, JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(
            WebFilterExchange webFilterExchange,
            Authentication authentication) {

        OAuth2AuthenticationToken oauth2Auth = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauth2Auth.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        return userRepository.findByEmail((String) attributes.get(EMAIL))
                .switchIfEmpty(createUser(oauth2Auth))
                .flatMap(user -> {
                    ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                    response.setStatusCode(HttpStatus.OK);
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                    // Create token or handle session
                    OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
                    String tokenValue = token.getName(); // Or generate JWT

                    Map<String, String> result = new HashMap<>();
                    result.put("token", tokenValue);

                    DataBuffer buffer = null;
                    try {
                        buffer = response.bufferFactory()
                                .wrap(new ObjectMapper().writeValueAsBytes(result));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    return response.writeWith(Mono.just(buffer));
                });
    }

    private Mono<User> createUser(OAuth2AuthenticationToken oauth2Auth) {
        OAuth2User oauth2User = oauth2Auth.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        User user = new User();
        user.setEmail((String) attributes.get(EMAIL));
        user.setName((String) attributes.get("name"));
        user.setPictureUrl((String) attributes.get("picture"));
        user.setProvider(oauth2Auth.getAuthorizedClientRegistrationId());
        user.setProviderId((String) attributes.get("sub"));
        user.setRoles(Collections.singletonList("ROLE_USER"));

        return userRepository.save(user);
    }
}