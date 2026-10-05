package com.coding.dto.response;

import java.math.BigDecimal;

public record DashboardMetricsProjection(
        BigDecimal totalRevenue,
        long totalOrders,
        long paidOrders,
        long pendingOrders,
        long cancelledOrders
) {}
