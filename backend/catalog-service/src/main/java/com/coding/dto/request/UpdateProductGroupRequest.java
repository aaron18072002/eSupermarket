package com.coding.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateProductGroupRequest(

        @NotBlank(message = "Product group name is required")
        String name

) {}
