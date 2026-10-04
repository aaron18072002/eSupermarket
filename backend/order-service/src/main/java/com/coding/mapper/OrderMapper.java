package com.coding.mapper;

import com.coding.dto.request.CreateOrderRequest;
import com.coding.dto.request.OrderItemRequest;
import com.coding.dto.response.OrderItemResponse;
import com.coding.dto.response.OrderResponse;
import com.coding.model.Order;
import com.coding.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderCode", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentQrUrl", ignore = true)
    @Mapping(target = "paidAt", ignore = true)
    @Mapping(target = "items", ignore = true)
    Order toEntity(CreateOrderRequest request);

    OrderResponse toResponse(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(request))")
    OrderItem toItemEntity(OrderItemRequest request);

    OrderItemResponse toItemResponse(OrderItem item);

    default BigDecimal calculateSubtotal(OrderItemRequest request) {
        if (request == null || request.unitPrice() == null || request.quantity() == null) {
            return BigDecimal.ZERO;
        }
        return request.unitPrice().multiply(BigDecimal.valueOf(request.quantity()));
    }

}
