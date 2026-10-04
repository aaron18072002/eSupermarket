package com.coding.controller;

import com.coding.dto.request.CreateOrderRequest;
import com.coding.dto.response.ApiResponse;
import com.coding.dto.response.DashboardSummaryResponse;
import com.coding.dto.response.OrderResponse;
import com.coding.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Order Management", description = "Endpoints for order creation, order tracking, and dashboard analytics")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @Operation(summary = "Create a new order and generate VietQR payment link")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = this.orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<OrderResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Order created successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get order details by order ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> readOrderById(@PathVariable UUID id) {
        OrderResponse response = this.orderService.readOrderById(id);
        return ResponseEntity.ok(
                ApiResponse.<OrderResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Order retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get order status by order code (used for checkout polling)")
    @GetMapping("/code/{orderCode}")
    public ResponseEntity<ApiResponse<OrderResponse>> readOrderByOrderCode(@PathVariable String orderCode) {
        OrderResponse response = this.orderService.readOrderByOrderCode(orderCode);
        return ResponseEntity.ok(
                ApiResponse.<OrderResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Order status retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get orders by customer user ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> readOrdersByUserId(@PathVariable UUID userId) {
        List<OrderResponse> response = this.orderService.readOrdersByUserId(userId);
        return ResponseEntity.ok(
                ApiResponse.<List<OrderResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("User orders retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get all orders (Admin)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> readAllOrders() {
        List<OrderResponse> response = this.orderService.readAllOrders();
        return ResponseEntity.ok(
                ApiResponse.<List<OrderResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("All orders retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get revenue and order metrics for the Admin Dashboard")
    @GetMapping("/dashboard/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> readDashboardSummary() {
        DashboardSummaryResponse response = this.orderService.readDashboardSummary();
        return ResponseEntity.ok(
                ApiResponse.<DashboardSummaryResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Dashboard summary retrieved successfully")
                        .data(response)
                        .build()
        );
    }

}
