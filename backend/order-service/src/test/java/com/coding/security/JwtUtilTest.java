package com.coding.security;

import com.coding.exception.AccessDeniedException;
import com.coding.exception.UnauthorizedException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private SecretKey secretKey;
    private static final String SECRET = "testSecretKeyForOrderServiceTestingPurposes123";

    @BeforeEach
    void setUp() {
        this.jwtUtil = new JwtUtil(SECRET);
        this.secretKey = JwtUtil.deriveSigningKey(SECRET);
    }

    private String createToken(List<String> roles) {
        return Jwts.builder()
                .subject("00000000-0000-0000-0000-000000000001")
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(secretKey)
                .compact();
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when auth header is missing or malformed")
    void testValidateAdminRoleMissingHeader() {
        assertThrows(UnauthorizedException.class, () -> jwtUtil.validateAdminRole(null));
        assertThrows(UnauthorizedException.class, () -> jwtUtil.validateAdminRole(""));
        assertThrows(UnauthorizedException.class, () -> jwtUtil.validateAdminRole("Basic 123"));
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when user lacks ROLE_ADMIN")
    void testValidateAdminRoleNonAdmin() {
        String token = createToken(List.of("ROLE_CUSTOMER"));
        assertThrows(AccessDeniedException.class, () -> jwtUtil.validateAdminRole("Bearer " + token));
    }

    @Test
    @DisplayName("Should pass validation when user has ROLE_ADMIN")
    void testValidateAdminRoleSuccess() {
        String token = createToken(List.of("ROLE_ADMIN", "ROLE_CUSTOMER"));
        assertDoesNotThrow(() -> jwtUtil.validateAdminRole("Bearer " + token));
    }
}
