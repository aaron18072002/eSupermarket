package com.coding.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateBrandRequest(

        @NotBlank(message = "Brand name is required")
        String name,

        String country

) {}
