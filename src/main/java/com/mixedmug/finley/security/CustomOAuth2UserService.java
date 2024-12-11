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

import java.util.Collections;
import java.util.Map;

import static com.mixedmug.finley.security.JwtUtil.EMAIL;

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
                    String provider = userRequest.getClientRegistration().getRegistrationId();
                    Map<String, Object> attributes = oauth2User.getAttributes();

                    return userRepository.findByEmail((String) attributes.get(EMAIL))
                            .switchIfEmpty(Mono.defer(() -> {
                                User newUser = new User();
                                if ("google".equals(provider)) {
                                    newUser.setEmail((String) attributes.get("email"));
                                    newUser.setName((String) attributes.get("name"));
                                    newUser.setPictureUrl((String) attributes.get("picture"));
                                    newUser.setProvider(provider);
                                    newUser.setProviderId((String) attributes.get("sub"));
                                    newUser.setRoles(Collections.singletonList("ROLE_USER"));
                                }
                                return userRepository.save(newUser);
                            }))
                            .thenReturn(oauth2User);
                });
    }
}