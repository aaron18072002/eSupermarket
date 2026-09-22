package com.coding.api.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${application.jwt.secret:default-secret-key-eSupermarket-secret-key-must-be-at-least-256-bits-long-123456}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
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

    /**
     * Extracts the subject (User ID / username) from the token.
     */
    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extracts user roles claim as a comma-separated string.
     */
    @SuppressWarnings("unchecked")
    public String extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object rolesObj = claims.get("roles");
        if (rolesObj == null) {
            rolesObj = claims.get("role");
        }

        if (rolesObj instanceof List<?>) {
            return String.join(",", ((List<?>) rolesObj).stream().map(Object::toString).toList());
        } else if (rolesObj != null) {
            return rolesObj.toString();
        }
        return "";
    }

    /**
     * Checks if the token has expired.
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractAllClaims(token).getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    /**
     * Validates if the token signature is authentic and token is not expired.
     */
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
