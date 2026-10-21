package com.coding.service;

import com.coding.dto.request.CreateOrderRequest;
import com.coding.dto.request.OrderItemRequest;
import com.coding.dto.response.DashboardMetricsProjection;
import com.coding.dto.response.DashboardSummaryResponse;
import com.coding.dto.response.OrderResponse;
import com.coding.mapper.OrderMapper;
import com.coding.model.Order;
import com.coding.model.OrderItem;
import com.coding.model.OrderStatus;
import com.coding.model.PaymentMethod;
import com.coding.repository.OrderRepository;
import com.coding.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        this.orderService = new OrderServiceImpl(orderRepository, orderMapper);
    }

    @Test
    @DisplayName("Should create cash/COD order successfully with PROCESSING status")
    void testCreateOrderCash() {
        UUID userId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(
                userId,
                "John Doe",
                "john@example.com",
                "+84901234567",
                "123 Nguyen Hue, District 1, HCMC",
                PaymentMethod.COD,
                "Leave at front door",
                List.of(new OrderItemRequest(
                        UUID.randomUUID(),
                        "Fresh Milk 1L",
                        "SKU-MILK-001",
                        "http://image.jpg",
                        BigDecimal.valueOf(25000),
                        2
                ))
        );

        Order orderEntity = new Order();
        OrderItem orderItem = OrderItem.builder()
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(25000))
                .subtotal(BigDecimal.valueOf(50000))
                .build();

        when(orderMapper.toEntity(any(CreateOrderRequest.class))).thenReturn(orderEntity);
        when(orderMapper.toItemEntity(any(OrderItemRequest.class))).thenReturn(orderItem);
        when(orderRepository.existsByOrderCode(any())).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toResponse(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            return new OrderResponse(
                    UUID.randomUUID(),
                    o.getOrderCode(),
                    o.getUserId(),
                    o.getCustomerName(),
                    "john@example.com",
                    o.getCustomerPhone(),
                    o.getShippingAddress(),
                    o.getTotalAmount(),
                    o.getStatus(),
                    o.getPaymentMethod(),
                    o.getNotes(),
                    o.getPaidAt(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    List.of()
            );
        });

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(OrderStatus.PROCESSING, response.status());
        assertEquals(PaymentMethod.COD, response.paymentMethod());
        assertEquals(BigDecimal.valueOf(50000), response.totalAmount());
        assertNotNull(response.orderCode());
        assertTrue(response.orderCode().startsWith("ORD"));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should retrieve dashboard summary via single-query projection")
    void testReadDashboardSummary() {
        DashboardMetricsProjection projection = new DashboardMetricsProjection(
                BigDecimal.valueOf(1500000), 20L, 15L, 3L, 2L
        );
        when(orderRepository.getDashboardMetrics()).thenReturn(projection);
        when(orderRepository.findTop10ByOrderByCreatedAtDesc()).thenReturn(List.of());

        DashboardSummaryResponse summary = orderService.readDashboardSummary();

        assertNotNull(summary);
        assertEquals(BigDecimal.valueOf(1500000), summary.totalRevenue());
        assertEquals(20L, summary.totalOrders());
        assertEquals(15L, summary.paidOrders());
        assertEquals(3L, summary.pendingOrders());
        assertEquals(2L, summary.cancelledOrders());
        assertTrue(summary.recentOrders().isEmpty());
    }

}
