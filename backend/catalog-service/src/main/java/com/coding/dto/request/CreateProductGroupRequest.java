package com.coding.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateProductGroupRequest(

        @NotBlank(message = "Product group name is required")
        String name

) {}
