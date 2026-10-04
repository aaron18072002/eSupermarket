package com.coding.dto.response;

import com.coding.model.OrderStatus;
import com.coding.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String orderCode,
        UUID userId,
        String customerName,
        String customerEmail,
        String customerPhone,
        String shippingAddress,
        BigDecimal totalAmount,
        OrderStatus status,
        PaymentMethod paymentMethod,
        String paymentQrUrl,
        String notes,
        LocalDateTime paidAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<OrderItemResponse> items
) {}
