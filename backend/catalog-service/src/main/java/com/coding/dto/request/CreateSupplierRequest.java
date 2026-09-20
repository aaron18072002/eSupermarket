package com.coding.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateSupplierRequest(

        @NotBlank(message = "Supplier name is required")
        String name

) {}
