package com.pavlomoskalenko.urlshortener.dto;

import com.pavlomoskalenko.urlshortener.entity.Role;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@RequiredArgsConstructor
public class UserResponse {
    private final Long id;
    private final String username;
    private final Set<Role> roles;
}
