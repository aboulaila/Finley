package com.mixedmug.finley.security;

import com.mixedmug.finley.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@AllArgsConstructor
public class FinleyUserDetails implements UserDetails {
    private final String email;
    private final String username;
    private final String password;
    private final Collection<GrantedAuthority> authorities;


    public FinleyUserDetails(User user) {
        this.email = user.getEmail();
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.authorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        this.authorities.add(authority);
    }
}