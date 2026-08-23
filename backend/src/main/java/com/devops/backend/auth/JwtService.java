package com.devops.backend.auth;

import com.devops.backend.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMinutes;

    public JwtService(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.expiration-minutes}") long expirationMinutes) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .signWith(signingKey)
                .compact();
    }

    public SecretKey getSigningKey() {
        return signingKey;
    }
}
