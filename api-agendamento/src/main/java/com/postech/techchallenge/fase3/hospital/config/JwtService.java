package com.postech.techchallenge.fase3.hospital.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap; // Import HashMap
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of("ROLE_USER")); // Add roles claim
        claims.put(Claims.SUBJECT, "test-user"); // Explicitly set subject in claims

        return Jwts.builder()
                .setClaims(claims) // Set all claims at once
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.ofEpochSecond(expiration)))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public boolean isTokenValid(String token) {

        try {

            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public Claims getClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
