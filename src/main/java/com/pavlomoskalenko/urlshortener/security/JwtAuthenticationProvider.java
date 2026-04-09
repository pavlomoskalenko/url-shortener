package com.pavlomoskalenko.urlshortener.security;

import com.pavlomoskalenko.urlshortener.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;

    @Override
    public @Nullable Authentication authenticate(@NonNull Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authToken = (JwtAuthenticationToken) authentication;
        if (authToken.getCredentials() == null) {
            throw new BadCredentialsException("No credentials provided");
        }

        String token = authToken.getCredentials().toString();
        if (jwtService.hasTokenExpired(token)) {
            throw new BadCredentialsException("Token has expired");
        }

        String username = jwtService.extractUsername(token);
        if (username == null) {
            throw new BadCredentialsException("Token is invalid");
        }

        return new JwtAuthenticationToken(
                username, null, jwtService.extractClaims(token), jwtService.extractRoles(token));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class);
    }
}
