package com.coding.service;

import com.coding.dto.request.CreateOrderRequest;
import com.coding.dto.request.OrderItemRequest;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplVietQrTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        this.orderService = new OrderServiceImpl(orderRepository, orderMapper);
        ReflectionTestUtils.setField(orderService, "bankAccount", "10000788121");
        ReflectionTestUtils.setField(orderService, "bankName", "TPBank");
        ReflectionTestUtils.setField(orderService, "accountHolder", "NGUYEN THANH ANH");
        ReflectionTestUtils.setField(orderService, "qrTemplate", "https://vietqr.app/img");
    }

    @Test
    @DisplayName("Should generate correct VietQR URL matching vietqr.app specification")
    void testVietQrUrlFormat() {
        UUID userId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(
                userId,
                "John Doe",
                "john@example.com",
                "+84901234567",
                "123 Street",
                PaymentMethod.VIETQR,
                "Urgent delivery",
                List.of(new OrderItemRequest(
                        UUID.randomUUID(),
                        "Milk",
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
                    o.getPaymentQrUrl(),
                    o.getNotes(),
                    o.getPaidAt(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    List.of()
            );
        });

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response.paymentQrUrl());
        assertTrue(response.paymentQrUrl().startsWith("https://vietqr.app/img?bank=TPBank&acc=10000788121&template=compact&amount=50000&des="));
        assertTrue(response.paymentQrUrl().contains("&showinfo=true&holder=NGUYEN%20THANH%20ANH"));
    }

    @Test
    @DisplayName("Should retrieve dashboard summary via single-query projection")
    void testReadDashboardSummary() {
        var projection = new com.coding.dto.response.DashboardMetricsProjection(
                BigDecimal.valueOf(1500000), 20L, 15L, 3L, 2L
        );
        when(orderRepository.getDashboardMetrics()).thenReturn(projection);
        when(orderRepository.findTop10ByOrderByCreatedAtDesc()).thenReturn(List.of());

        var summary = orderService.readDashboardSummary();

        assertNotNull(summary);
        assertEquals(BigDecimal.valueOf(1500000), summary.totalRevenue());
        assertEquals(20L, summary.totalOrders());
        assertEquals(15L, summary.paidOrders());
        assertEquals(3L, summary.pendingOrders());
        assertEquals(2L, summary.cancelledOrders());
        assertTrue(summary.recentOrders().isEmpty());
    }
}
