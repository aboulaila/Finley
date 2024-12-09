package com.mixedmug.finley.security;

import com.mixedmug.finley.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class FinleyUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public FinleyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with email: " + email)))
                .map(FinleyUserDetails::new);
    }
}