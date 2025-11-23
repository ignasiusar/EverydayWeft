// src/main/java/com/example/everydayweft/security/JwtUtil.java
package com.example.everydayweft.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final Key key = Keys.hmacShaKeyFor(
            System.getenv().getOrDefault("JWT_SECRET", "dev-secret-make-it-long-and-random-please-32+chars").getBytes()
    );

    private final long accessTokenValidityMs = 1000 * 60 * 60; // 1 hour

    public String generateAccessToken(String subject, Map<String,Object> claims) {
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidityMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }
}
