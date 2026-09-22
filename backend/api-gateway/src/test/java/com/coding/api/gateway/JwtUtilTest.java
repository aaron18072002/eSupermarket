package com.coding.api.gateway;

import com.coding.api.gateway.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET = "liverpoolvodich123456";
    private JwtUtil jwtUtil;
    private SecretKey signingKey;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
        signingKey = JwtUtil.deriveSigningKey(SECRET);
    }

    @Test
    @DisplayName("Should extract userId and roles from valid JWT token using liverpoolvodich123456")
    void shouldExtractClaimsFromValidToken() {
        String token = Jwts.builder()
                .subject("user-uuid-12345")
                .claim("roles", List.of("ROLE_ADMIN", "ROLE_CUSTOMER"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000)) // +1 hour
                .signWith(signingKey)
                .compact();

        assertEquals("user-uuid-12345", jwtUtil.extractUserId(token));
        assertEquals("ROLE_ADMIN,ROLE_CUSTOMER", jwtUtil.extractRoles(token));
        assertTrue(jwtUtil.validateToken(token));
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    @DisplayName("Should throw ExpiredJwtException when token is expired")
    void shouldThrowWhenTokenExpired() {
        String expiredToken = Jwts.builder()
                .subject("user-uuid-12345")
                .issuedAt(new Date(System.currentTimeMillis() - 7200000)) // -2 hours
                .expiration(new Date(System.currentTimeMillis() - 3600000)) // -1 hour
                .signWith(signingKey)
                .compact();

        assertThrows(ExpiredJwtException.class, () -> jwtUtil.extractAllClaims(expiredToken));
        assertTrue(jwtUtil.isTokenExpired(expiredToken));
        assertFalse(jwtUtil.validateToken(expiredToken));
    }

    @Test
    @DisplayName("Should reject token signed with different key (SignatureException)")
    void shouldRejectTamperedToken() {
        SecretKey otherKey = JwtUtil.deriveSigningKey("manchesterunitedvodich123456");
        String forgedToken = Jwts.builder()
                .subject("attacker")
                .signWith(otherKey)
                .compact();

        assertThrows(SignatureException.class, () -> jwtUtil.extractAllClaims(forgedToken));
        assertFalse(jwtUtil.validateToken(forgedToken));
    }
}
