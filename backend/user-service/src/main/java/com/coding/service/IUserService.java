package com.coding.service;

import com.coding.dto.request.ChangePasswordRequest;
import com.coding.dto.request.UpdateUserRequest;
import com.coding.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface IUserService {

    UserResponse readUserById(UUID id);

    UserResponse readUserByEmail(String email);

    List<UserResponse> readAllUsers();

    UserResponse updateUser(UUID id, UpdateUserRequest request);

    void changePassword(UUID id, ChangePasswordRequest request);

    void deleteUserById(UUID id);

}
