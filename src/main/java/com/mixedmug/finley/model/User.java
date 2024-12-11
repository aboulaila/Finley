package com.mixedmug.finley.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Table("users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private Long id;
    private String name;
    private String pictureUrl;
    private String password_hash;
    private String provider;
    private String providerId;
    private String email;
    private List<String> roles;
}