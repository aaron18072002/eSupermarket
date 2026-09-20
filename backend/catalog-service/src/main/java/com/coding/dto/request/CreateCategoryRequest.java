package com.coding.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateCategoryRequest(

        @NotBlank(message = "Category name is required")
        String name,

        UUID parentId

) {}
