package com.mixedmug.finley.security;

import com.mixedmug.finley.model.User;
import com.mixedmug.finley.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomOAuth2UserService implements ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    @Autowired
    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) {
        final DefaultReactiveOAuth2UserService delegate = new DefaultReactiveOAuth2UserService();
        return delegate.loadUser(userRequest)
                .flatMap(oauth2User -> {
                    String email = oauth2User.getAttribute("email");
                    if (email == null) {
                        return Mono.error(new RuntimeException("Email not found from OAuth2 provider"));
                    }

                    return userRepository.findByEmail(email)
                            .switchIfEmpty(Mono.defer(() -> {
                                User newUser = new User();
                                newUser.setEmail(email);
                                newUser.setUsername(oauth2User.getAttribute("name"));
                                newUser.setRole("ROLE_USER");
                                return userRepository.save(newUser);
                            }))
                            .thenReturn(oauth2User);
                });
    }
}