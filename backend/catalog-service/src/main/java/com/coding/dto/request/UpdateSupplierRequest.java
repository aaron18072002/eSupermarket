package com.coding.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateSupplierRequest(

        @NotBlank(message = "Supplier name is required")
        String name

) {}
