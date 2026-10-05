package com.coding.dto.response;

import com.coding.model.StockStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryResponse(
        UUID id,
        UUID productId,
        String productSku,
        String productName,
        String warehouseLocation,
        Integer quantityAvailable,
        Integer quantityReserved,
        Integer safetyStockLevel,
        StockStatus stockStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
