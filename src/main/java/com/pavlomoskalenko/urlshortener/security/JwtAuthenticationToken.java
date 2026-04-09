package com.pavlomoskalenko.urlshortener.security;

import io.jsonwebtoken.Claims;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String subject;
    private final String token;
    @Getter
    private final Claims claims;

    public JwtAuthenticationToken(String token) {
        super((Collection<? extends GrantedAuthority>) null);
        this.token = token;
        this.subject = null;
        this.claims = null;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(String subject, String token, Claims claims,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.subject = subject;
        this.token = token;
        this.claims = claims;
        setAuthenticated(true);
    }

    @Override
    public @Nullable Object getCredentials() {
        return token;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return subject;
    }

}
