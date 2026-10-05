package com.coding.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateInventoryRequest(
        @NotNull(message = "Product ID is required")
        UUID productId,

        @NotBlank(message = "Product SKU is required")
        String productSku,

        @NotBlank(message = "Product name is required")
        String productName,

        String warehouseLocation,

        @NotNull(message = "Quantity available is required")
        @Min(value = 0, message = "Quantity available cannot be negative")
        Integer quantityAvailable,

        @Min(value = 0, message = "Safety stock level cannot be negative")
        Integer safetyStockLevel
) {}
