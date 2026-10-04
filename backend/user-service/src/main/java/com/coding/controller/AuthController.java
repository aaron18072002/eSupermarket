package com.coding.controller;

import com.coding.dto.request.LoginRequest;
import com.coding.dto.request.RefreshTokenRequest;
import com.coding.dto.request.RegisterRequest;
import com.coding.dto.response.ApiResponse;
import com.coding.dto.response.AuthResponse;
import com.coding.dto.response.TokenRefreshResponse;
import com.coding.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Endpoints for user registration, login, token refresh, and logout")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @Operation(summary = "Register a new user account")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = this.authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<AuthResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("User registered successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Log in with email and password")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = this.authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Logged in successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Refresh access token using refresh token")
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        TokenRefreshResponse response = this.authService.refreshToken(request);
        return ResponseEntity.ok(
                ApiResponse.<TokenRefreshResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Token refreshed successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Log out user and revoke refresh token")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) RefreshTokenRequest request) {
        if (request != null && request.getRefreshToken() != null) {
            this.authService.logout(request.getRefreshToken());
        }
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Logged out successfully")
                        .build()
        );
    }

}
