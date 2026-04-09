package com.pavlomoskalenko.urlshortener.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${security.jwt.secret_key}")
    private String secretKey;

    @Value("${security.jwt.access_token_expiration}")
    private long accessTokenExpiration;

    @Value("${security.jwt.refresh_token_expiration}")
    private long refreshTokenExpiration;

    @Override
    public String generateAccessToken(String username, List<String> roles) {
        return generateToken(username, accessTokenExpiration, roles);
    }

    @Override
    public String generateRefreshToken(String username) {
        return generateToken(username, refreshTokenExpiration, Collections.emptyList());
    }

    @Override
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    @Override
    public Collection<? extends GrantedAuthority> extractRoles(String token) {
        String authorities = (String) extractClaims(token, (c) -> c.get("roles"));
        if (authorities == null || authorities.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(authorities.split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public boolean hasTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    private String generateToken(String username, long expiration, List<String> roles) {
        Date now = new Date();
        JwtBuilder builder = Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .claim("roles", String.join(",", roles))
                .signWith(getSignKey());

        return builder.compact();
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    private <T> T extractClaims(String token, Function<Claims, T> resolver) {
        Claims claims;
        try {
            claims = extractClaims(token);
        } catch (ClaimJwtException e) {
            claims = e.getClaims();
        }

        return resolver.apply(claims);
    }

    @Override
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
