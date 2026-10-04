package com.coding.service;

import com.coding.dto.request.CreateOrderRequest;
import com.coding.dto.response.DashboardSummaryResponse;
import com.coding.dto.response.OrderResponse;
import com.coding.dto.webhook.SepayWebhookPayload;

import java.util.List;
import java.util.UUID;

public interface IOrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse readOrderById(UUID id);

    OrderResponse readOrderByOrderCode(String orderCode);

    List<OrderResponse> readOrdersByUserId(UUID userId);

    List<OrderResponse> readAllOrders();

    DashboardSummaryResponse readDashboardSummary();

    boolean processSepayWebhook(SepayWebhookPayload payload);

}
