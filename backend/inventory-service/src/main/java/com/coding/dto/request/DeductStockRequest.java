package com.coding.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeductStockRequest(
        @NotNull(message = "Product ID is required")
        UUID productId,

        @NotNull(message = "Deduct quantity is required")
        @Min(value = 1, message = "Deduct quantity must be at least 1")
        Integer quantity
) {}
