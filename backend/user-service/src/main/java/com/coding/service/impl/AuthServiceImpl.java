package com.coding.service.impl;

import com.coding.dto.request.LoginRequest;
import com.coding.dto.request.RefreshTokenRequest;
import com.coding.dto.request.RegisterRequest;
import com.coding.dto.response.AuthResponse;
import com.coding.dto.response.TokenRefreshResponse;
import com.coding.exception.DuplicateResourceException;
import com.coding.exception.InvalidCredentialsException;
import com.coding.exception.TokenRefreshException;
import com.coding.mapper.UserMapper;
import com.coding.model.RefreshToken;
import com.coding.model.User;
import com.coding.model.UserStatus;
import com.coding.repository.RefreshTokenRepository;
import com.coding.repository.UserRepository;
import com.coding.security.JwtUtil;
import com.coding.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (this.userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered: " + request.getEmail());
        }

        User user = this.userMapper.toEntity(request);
        user.setPassword(this.passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(new HashSet<>(Set.of("ROLE_CUSTOMER")));

        User savedUser = this.userRepository.save(user);
        String accessToken = this.jwtUtil.generateAccessToken(savedUser);
        RefreshToken refreshToken = createRefreshToken(savedUser);

        log.info("Registered new user with email: {}", savedUser.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(this.jwtUtil.getAccessTokenExpirationMs())
                .user(this.userMapper.toResponse(savedUser))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = this.userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!this.passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidCredentialsException("Account is currently " + user.getStatus().name().toLowerCase());
        }

        String accessToken = this.jwtUtil.generateAccessToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(this.jwtUtil.getAccessTokenExpirationMs())
                .user(this.userMapper.toResponse(user))
                .build();
    }

    @Override
    public TokenRefreshResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = this.refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new TokenRefreshException("Refresh token was not found in the system"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            this.refreshTokenRepository.delete(refreshToken);
            throw new TokenRefreshException("Refresh token is expired or revoked. Please log in again.");
        }

        User user = refreshToken.getUser();
        String newAccessToken = this.jwtUtil.generateAccessToken(user);

        // Refresh token rotation: issue a new refresh token and update existing record
        refreshToken.setToken(this.jwtUtil.generateRefreshTokenString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(this.jwtUtil.getRefreshTokenExpirationMs()));
        RefreshToken updatedToken = this.refreshTokenRepository.save(refreshToken);

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(updatedToken.getToken())
                .tokenType("Bearer")
                .expiresIn(this.jwtUtil.getAccessTokenExpirationMs())
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            this.refreshTokenRepository.deleteByToken(refreshToken);
            log.info("Revoked refresh token upon logout");
        }
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken token = RefreshToken.builder()
                .token(this.jwtUtil.generateRefreshTokenString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(this.jwtUtil.getRefreshTokenExpirationMs()))
                .revoked(false)
                .build();

        return this.refreshTokenRepository.save(token);
    }

}
