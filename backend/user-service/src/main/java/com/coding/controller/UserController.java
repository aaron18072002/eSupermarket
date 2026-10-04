package com.coding.controller;

import com.coding.dto.request.ChangePasswordRequest;
import com.coding.dto.request.UpdateUserRequest;
import com.coding.dto.response.ApiResponse;
import com.coding.dto.response.UserResponse;
import com.coding.security.JwtUtil;
import com.coding.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "User Management", description = "Endpoints for user profile retrieval, user updates, and password changes")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Get current authenticated user profile")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> readCurrentUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        UUID userId = extractUserIdFromAuthHeader(authHeader);
        UserResponse response = this.userService.readUserById(userId);
        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Current user profile retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get user profile by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> readUserById(@PathVariable UUID id) {
        UserResponse response = this.userService.readUserById(id);
        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("User profile retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get all users (Admin)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> readAllUsers() {
        List<UserResponse> response = this.userService.readAllUsers();
        return ResponseEntity.ok(
                ApiResponse.<List<UserResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Users retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Update user profile details")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = this.userService.updateUser(id, request);
        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("User updated successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Change user password")
    @PutMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordRequest request) {
        this.userService.changePassword(id, request);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Password changed successfully")
                        .build()
        );
    }

    @Operation(summary = "Delete user by ID (Admin)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserById(@PathVariable UUID id) {
        this.userService.deleteUserById(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("User deleted successfully")
                        .build()
        );
    }

    private UUID extractUserIdFromAuthHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return UUID.fromString(this.jwtUtil.extractUserId(token));
        }
        throw new IllegalArgumentException("Invalid Authorization header format. Expected 'Bearer <token>'");
    }

}
