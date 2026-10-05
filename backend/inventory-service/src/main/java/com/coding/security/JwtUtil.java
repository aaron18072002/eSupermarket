package com.coding.security;

import com.coding.exception.AccessDeniedException;
import com.coding.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${application.jwt.secret}") String secret) {
        this.secretKey = deriveSigningKey(secret);
    }

    /**
     * Derives a deterministic 256-bit HMAC key from any input secret using SHA-256.
     * Matches the derivation algorithm used by API Gateway, User Service, and Order Service.
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
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts user roles list from token claims using explicit lambda expressions.
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof Collection<?> collection) {
            return collection.stream()
                    .map(role -> role.toString())
                    .toList();
        }
        return List.of();
    }

    /**
     * Validates that the authorization header contains a valid JWT with ROLE_ADMIN.
     * Throws UnauthorizedException if token is missing/invalid/expired.
     * Throws AccessDeniedException if authenticated user lacks administrator privileges.
     */
    public void validateAdminRole(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Missing or invalid Authorization header. Expected 'Bearer <token>'");
        }

        String token = authHeader.substring(7).trim();
        try {
            List<String> roles = extractRoles(token);
            boolean isAdmin = roles.stream().anyMatch(r ->
                    "ROLE_ADMIN".equalsIgnoreCase(r) || "ADMIN".equalsIgnoreCase(r));

            if (!isAdmin) {
                log.warn("Access denied: caller does not have ROLE_ADMIN. Assigned roles: {}", roles);
                throw new AccessDeniedException(
                        "Access denied: Administrator privileges required to access this endpoint"
                );
            }
        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired: {}", e.getMessage());
            throw new UnauthorizedException("Authentication failed: JWT token has expired");
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw new UnauthorizedException("Authentication failed: Invalid JWT token");
        }
    }

}
