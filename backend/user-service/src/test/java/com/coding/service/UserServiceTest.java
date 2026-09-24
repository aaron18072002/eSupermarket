package com.coding.service;

import com.coding.dto.request.ChangePasswordRequest;
import com.coding.dto.request.UpdateUserRequest;
import com.coding.dto.response.UserResponse;
import com.coding.exception.InvalidCredentialsException;
import com.coding.exception.ResourceNotFoundException;
import com.coding.mapper.UserMapper;
import com.coding.model.User;
import com.coding.model.UserStatus;
import com.coding.repository.RefreshTokenRepository;
import com.coding.repository.UserRepository;
import com.coding.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        sampleUser = User.builder()
                .id(userId)
                .email("alice@example.com")
                .password("encoded_pass")
                .fullName("Alice Wonderland")
                .status(UserStatus.ACTIVE)
                .roles(Set.of("ROLE_CUSTOMER"))
                .build();
    }

    @Test
    @DisplayName("Should read user by id")
    void testReadUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(userMapper.toResponse(sampleUser)).thenReturn(UserResponse.builder().id(userId).email("alice@example.com").build());

        UserResponse response = userService.readUserById(userId);

        assertNotNull(response);
        assertEquals("alice@example.com", response.getEmail());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user id not found")
    void testReadUserByIdNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.readUserById(userId));
    }

    @Test
    @DisplayName("Should update user profile")
    void testUpdateUser() {
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .fullName("Alice Updated")
                .phone("0123456789")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(userMapper.toResponse(sampleUser)).thenReturn(UserResponse.builder().id(userId).fullName("Alice Updated").build());

        UserResponse response = userService.updateUser(userId, updateRequest);

        assertNotNull(response);
        verify(userMapper).updateUserFromRequest(updateRequest, sampleUser);
        verify(userRepository).save(sampleUser);
    }

    @Test
    @DisplayName("Should change user password and invalidate sessions")
    void testChangePasswordSuccess() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("encoded_pass")
                .newPassword("new_secret_pass")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("encoded_pass", "encoded_pass")).thenReturn(true);
        when(passwordEncoder.encode("new_secret_pass")).thenReturn("new_encoded_pass");

        userService.changePassword(userId, request);

        verify(userRepository).save(sampleUser);
        verify(refreshTokenRepository).deleteByUser(sampleUser);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when old password is wrong")
    void testChangePasswordWrongCurrent() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("wrong_pass")
                .newPassword("new_secret_pass")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrong_pass", "encoded_pass")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.changePassword(userId, request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user and associated tokens")
    void testDeleteUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));

        userService.deleteUserById(userId);

        verify(refreshTokenRepository).deleteByUser(sampleUser);
        verify(userRepository).delete(sampleUser);
    }

}
