package com.coding.dto.response;

import java.util.List;

public record InventoryDashboardSummaryResponse(
        long totalTrackedItems,
        long inStockCount,
        long lowStockCount,
        long outOfStockCount,
        List<InventoryResponse> lowStockAlerts
) {}
