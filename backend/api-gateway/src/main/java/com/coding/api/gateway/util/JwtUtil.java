package com.coding.api.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${application.jwt.secret}") String secret) {
        this.secretKey = deriveSigningKey(secret);
    }

    /**
     * Derives a deterministic 256-bit HMAC key from any input secret using SHA-256 hashing.
     * This allows custom passphrase secrets of any length to satisfy HMAC-SHA256 requirements.
     */
    public static SecretKey deriveSigningKey(String secret) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha256.digest(secret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Parses and verifies signature & claims of a signed JWT token.
     * Throws JwtException subclasses (ExpiredJwtException, MalformedJwtException, etc.) if invalid.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
