package com.coding.dto.request;

import com.coding.model.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(

        UUID userId,

        @NotBlank(message = "Customer name is required")
        String customerName,

        String customerEmail,

        @NotBlank(message = "Customer phone number is required")
        String customerPhone,

        @NotBlank(message = "Shipping address is required")
        String shippingAddress,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        String notes,

        @NotEmpty(message = "Order must contain at least one item")
        @Valid
        List<OrderItemRequest> items

) {}
