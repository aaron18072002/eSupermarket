package com.coding.security;

import com.coding.model.User;
import com.coding.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secret = "liverpoolvodich123456";
    private final long accessExpiration = 3600000; // 1 hour
    private final long refreshExpiration = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(secret, accessExpiration, refreshExpiration);
    }

    @Test
    @DisplayName("Should generate valid access token containing user claims")
    void testGenerateAccessToken() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .email("john.doe@example.com")
                .fullName("John Doe")
                .status(UserStatus.ACTIVE)
                .roles(Set.of("ROLE_CUSTOMER"))
                .build();

        String token = jwtUtil.generateAccessToken(user);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(userId.toString(), jwtUtil.extractUserId(token));
    }

    @Test
    @DisplayName("Should reject invalid token")
    void testInvalidToken() {
        String invalidToken = "invalid.token.string";
        assertFalse(jwtUtil.validateToken(invalidToken));
    }

    @Test
    @DisplayName("Should generate non-empty refresh token string")
    void testGenerateRefreshTokenString() {
        String refreshToken = jwtUtil.generateRefreshTokenString();
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isBlank());
    }

}
