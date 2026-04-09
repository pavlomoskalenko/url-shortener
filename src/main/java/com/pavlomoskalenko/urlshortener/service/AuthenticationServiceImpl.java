package com.pavlomoskalenko.urlshortener.service;

import com.pavlomoskalenko.urlshortener.dao.RoleRepository;
import com.pavlomoskalenko.urlshortener.dao.UserRepository;
import com.pavlomoskalenko.urlshortener.dto.*;
import com.pavlomoskalenko.urlshortener.entity.Role;
import com.pavlomoskalenko.urlshortener.entity.User;
import com.pavlomoskalenko.urlshortener.exception.UsernameAlreadyExists;
import com.pavlomoskalenko.urlshortener.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Override
    public UserResponse register(RegistrationRequest registrationRequest) {
        if (userRepository.existsUserByUsername(registrationRequest.getUsername())) {
            throw new UsernameAlreadyExists("User with such username already exists");
        }

        Role userRole = roleRepository.findRoleByValue("USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());
        registrationRequest.setPassword(encodedPassword);
        User user = mapToUser(registrationRequest, Set.of(userRole));

        return userMapper.mapToResponse(userRepository.save(user));
    }

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        User user = userRepository.findUserByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User with such username doesn't exist"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }

        String accessToken = jwtService.generateAccessToken(user.getUsername(), mapToSecurityRoles(user.getRoles()));
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        return new TokenResponse(accessToken, refreshToken);
    }

    @Override
    public TokenResponse refresh(RefreshRequest refreshRequest) {
        String token = refreshRequest.getRefreshToken();

        if (jwtService.hasTokenExpired(token)) {
            throw new BadCredentialsException("Refresh token has expired");
        }

        String username = jwtService.extractUsername(token);
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with such username doesn't exist"));

        String accessToken = jwtService.generateAccessToken(user.getUsername(), mapToSecurityRoles(user.getRoles()));

        return new TokenResponse(accessToken, null);
    }

    private User mapToUser(RegistrationRequest registrationRequest, Set<Role> roles) {
        return new User(registrationRequest.getUsername(), registrationRequest.getPassword(), roles);
    }

    private List<String> mapToSecurityRoles(Set<Role> roles) {
        return roles.stream()
                .map(r -> "ROLE_" + r.getValue())
                .toList();
    }

}
