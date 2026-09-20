package com.coding.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateBrandRequest(

        @NotBlank(message = "Brand name is required")
        String name,

        String country

) {}
