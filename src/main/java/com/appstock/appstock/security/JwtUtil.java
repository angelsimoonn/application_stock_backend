package com.appstock.appstock.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Mejor que esté en properties; aquí ejemplo directo
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION_MS = 1000 * 60 * 60 * 24; // 24h

    public String generateToken(String username, String rol) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .claim("rol", rol) // String
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) {
        JwtParser parser = Jwts.parser().verifyWith((SecretKey) key).build();
        return parser.parseSignedClaims(token).getPayload();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRol(String token) {
        Object r = parseClaims(token).get("rol");
        return r != null ? r.toString() : null;
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token); // lanzará excepción si inválido
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
