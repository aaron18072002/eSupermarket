package com.coding.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        BigDecimal totalRevenue,
        long totalOrders,
        long paidOrders,
        long pendingOrders,
        long cancelledOrders,
        List<OrderResponse> recentOrders
) {}
