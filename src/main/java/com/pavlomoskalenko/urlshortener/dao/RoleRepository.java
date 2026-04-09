package com.pavlomoskalenko.urlshortener.dao;

import com.pavlomoskalenko.urlshortener.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findRoleByValue(String value);
}
