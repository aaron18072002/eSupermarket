package com.coding.dto.response;

public record StockSummaryProjection(
        long totalItems,
        long inStockItems,
        long lowStockItems,
        long outOfStockItems
) {}
