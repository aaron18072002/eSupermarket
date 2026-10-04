package com.coding.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Valid password meeting all criteria should pass validation")
    void testValidPassword() {
        RegisterRequest request = RegisterRequest.builder()
                .email("john@example.com")
                .password("Admin@123")
                .fullName("John Doe")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Valid password should not produce violations");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "admin@123",   // No uppercase
            "Admin12345",   // No special character
            "Admin@@@@@",   // No digit
            "Ad1!",         // Less than 6 characters
            "Admin 1234",   // Space is not a valid special character
            ""              // Blank
    })
    @DisplayName("Invalid passwords violating complexity rules should fail validation")
    void testInvalidPasswords(String invalidPassword) {
        RegisterRequest request = RegisterRequest.builder()
                .email("john@example.com")
                .password(invalidPassword)
                .fullName("John Doe")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Password '" + invalidPassword + "' should fail validation");
    }

    @Test
    @DisplayName("ChangePasswordRequest newPassword must satisfy the same complexity rules")
    void testChangePasswordValidation() {
        ChangePasswordRequest invalidRequest = ChangePasswordRequest.builder()
                .oldPassword("OldPass@123")
                .newPassword("weakpass")
                .build();

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty(), "Weak new password should fail validation");

        ChangePasswordRequest validRequest = ChangePasswordRequest.builder()
                .oldPassword("OldPass@123")
                .newPassword("NewStrong@456")
                .build();

        Set<ConstraintViolation<ChangePasswordRequest>> validViolations = validator.validate(validRequest);
        assertTrue(validViolations.isEmpty(), "Strong new password should pass validation");
    }
}
