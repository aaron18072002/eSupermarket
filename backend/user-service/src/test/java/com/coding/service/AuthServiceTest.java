package com.coding.service;

import com.coding.dto.request.LoginRequest;
import com.coding.dto.request.RefreshTokenRequest;
import com.coding.dto.request.RegisterRequest;
import com.coding.dto.response.AuthResponse;
import com.coding.dto.response.TokenRefreshResponse;
import com.coding.dto.response.UserResponse;
import com.coding.exception.DuplicateResourceException;
import com.coding.exception.InvalidCredentialsException;
import com.coding.mapper.UserMapper;
import com.coding.model.RefreshToken;
import com.coding.model.User;
import com.coding.model.UserStatus;
import com.coding.repository.RefreshTokenRepository;
import com.coding.repository.UserRepository;
import com.coding.security.JwtUtil;
import com.coding.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private User sampleUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        sampleUser = User.builder()
                .id(userId)
                .email("test@example.com")
                .password("encoded_password")
                .fullName("Test User")
                .status(UserStatus.ACTIVE)
                .roles(Set.of("ROLE_CUSTOMER"))
                .build();

        registerRequest = RegisterRequest.builder()
                .email("test@example.com")
                .password("raw_password")
                .fullName("Test User")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("raw_password")
                .build();
    }

    @Test
    @DisplayName("Should register new user successfully")
    void testRegisterSuccess() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userMapper.toEntity(registerRequest)).thenReturn(sampleUser);
        when(passwordEncoder.encode("raw_password")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtUtil.generateAccessToken(sampleUser)).thenReturn("sample.access.token");
        when(jwtUtil.generateRefreshTokenString()).thenReturn("sample-refresh-token");
        when(jwtUtil.getRefreshTokenExpirationMs()).thenReturn(86400000L);
        when(jwtUtil.getAccessTokenExpirationMs()).thenReturn(3600000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponse(sampleUser)).thenReturn(UserResponse.builder().email("test@example.com").build());

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("sample.access.token", response.getAccessToken());
        assertEquals("sample-refresh-token", response.getRefreshToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void testRegisterDuplicateEmail() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void testLoginSuccess() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("raw_password", "encoded_password")).thenReturn(true);
        when(jwtUtil.generateAccessToken(sampleUser)).thenReturn("jwt.token.here");
        when(jwtUtil.generateRefreshTokenString()).thenReturn("refresh.token.here");
        when(jwtUtil.getRefreshTokenExpirationMs()).thenReturn(86400000L);
        when(jwtUtil.getAccessTokenExpirationMs()).thenReturn(3600000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponse(sampleUser)).thenReturn(UserResponse.builder().email("test@example.com").build());

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt.token.here", response.getAccessToken());
        verify(passwordEncoder, times(1)).matches("raw_password", "encoded_password");
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException with wrong password")
    void testLoginWrongPassword() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("raw_password", "encoded_password")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void testRefreshTokenSuccess() {
        RefreshToken refreshToken = RefreshToken.builder()
                .token("valid-refresh-token")
                .user(sampleUser)
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("valid-refresh-token")).thenReturn(Optional.of(refreshToken));
        when(jwtUtil.generateAccessToken(sampleUser)).thenReturn("new.access.token");
        when(jwtUtil.generateRefreshTokenString()).thenReturn("new-rotated-refresh-token");
        when(jwtUtil.getRefreshTokenExpirationMs()).thenReturn(86400000L);
        when(jwtUtil.getAccessTokenExpirationMs()).thenReturn(3600000L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TokenRefreshResponse response = authService.refreshToken(
                RefreshTokenRequest.builder().refreshToken("valid-refresh-token").build()
        );

        assertNotNull(response);
        assertEquals("new.access.token", response.getAccessToken());
        assertEquals("new-rotated-refresh-token", response.getRefreshToken());
    }

    @Test
    @DisplayName("Should logout and delete refresh token")
    void testLogout() {
        authService.logout("refresh-token-to-revoke");
        verify(refreshTokenRepository, times(1)).deleteByToken("refresh-token-to-revoke");
    }

}
