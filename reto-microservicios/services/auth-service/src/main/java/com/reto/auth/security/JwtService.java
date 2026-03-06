package com.reto.auth.security;

import com.reto.auth.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration-minutes}")
    private int expirationMinutes;

    public String generateToken(UserEntity user) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("userId", user.getId())
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis() + expirationMinutes * 60 * 1000L
                ))
                .signWith(key)
                .compact();
    }
}
