package com.coding.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateStockRequest(
        @NotNull(message = "Quantity available is required")
        @Min(value = 0, message = "Quantity available cannot be negative")
        Integer quantityAvailable,

        @Min(value = 0, message = "Quantity reserved cannot be negative")
        Integer quantityReserved,

        @Min(value = 0, message = "Safety stock level cannot be negative")
        Integer safetyStockLevel,

        String warehouseLocation
) {}
