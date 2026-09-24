package com.coding.service.impl;

import com.coding.dto.request.ChangePasswordRequest;
import com.coding.dto.request.UpdateUserRequest;
import com.coding.dto.response.UserResponse;
import com.coding.exception.InvalidCredentialsException;
import com.coding.exception.ResourceNotFoundException;
import com.coding.mapper.UserMapper;
import com.coding.model.User;
import com.coding.repository.RefreshTokenRepository;
import com.coding.repository.UserRepository;
import com.coding.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponse readUserById(UUID id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return this.userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse readUserByEmail(String email) {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return this.userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> readAllUsers() {
        return this.userRepository.findAll().stream()
                .map(this.userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        this.userMapper.updateUserFromRequest(request, user);
        User updatedUser = this.userRepository.save(user);

        log.info("Updated user profile for id: {}", id);
        return this.userMapper.toResponse(updatedUser);
    }

    @Override
    public void changePassword(UUID id, ChangePasswordRequest request) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!this.passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password does not match");
        }

        user.setPassword(this.passwordEncoder.encode(request.getNewPassword()));
        this.userRepository.save(user);

        // Invalidate all active refresh tokens for security
        this.refreshTokenRepository.deleteByUser(user);
        log.info("Password changed and sessions invalidated for user id: {}", id);
    }

    @Override
    public void deleteUserById(UUID id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        this.refreshTokenRepository.deleteByUser(user);
        this.userRepository.delete(user);
        log.info("Deleted user and revoked tokens for id: {}", id);
    }

}
