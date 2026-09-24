package com.coding.service;

import com.coding.dto.request.LoginRequest;
import com.coding.dto.request.RefreshTokenRequest;
import com.coding.dto.request.RegisterRequest;
import com.coding.dto.response.AuthResponse;
import com.coding.dto.response.TokenRefreshResponse;

public interface IAuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    TokenRefreshResponse refreshToken(RefreshTokenRequest request);

    void logout(String refreshToken);

}
