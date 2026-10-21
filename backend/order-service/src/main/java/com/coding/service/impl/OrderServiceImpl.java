package com.coding.service.impl;

import com.coding.dto.request.CreateOrderRequest;
import com.coding.dto.response.DashboardMetricsProjection;
import com.coding.dto.response.DashboardSummaryResponse;
import com.coding.dto.response.OrderResponse;
import com.coding.exception.ResourceNotFoundException;
import com.coding.mapper.OrderMapper;
import com.coding.model.Order;
import com.coding.model.OrderItem;
import com.coding.model.OrderStatus;
import com.coding.model.PaymentMethod;
import com.coding.repository.OrderRepository;
import com.coding.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = this.orderMapper.toEntity(request);
        String orderCode = generateUniqueOrderCode();
        order.setOrderCode(orderCode);

        // Map items and calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (var itemReq : request.items()) {
            OrderItem item = this.orderMapper.toItemEntity(itemReq);
            totalAmount = totalAmount.add(item.getSubtotal());
            order.addItem(item);
        }
        order.setTotalAmount(totalAmount);

        // Set status and payment details for cash / COD order
        PaymentMethod method = request.paymentMethod() != null ? request.paymentMethod() : PaymentMethod.COD;
        order.setPaymentMethod(method);
        order.setStatus(OrderStatus.PROCESSING);

        Order savedOrder = this.orderRepository.save(order);
        log.info("Created new cash order [{}] with total amount: {} VND", orderCode, totalAmount);
        return this.orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse readOrderById(UUID id) {
        Order order = this.orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        return this.orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse readOrderByOrderCode(String orderCode) {
        Order order = this.orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with code: " + orderCode));
        return this.orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> readOrdersByUserId(UUID userId) {
        return this.orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(order -> this.orderMapper.toResponse(order))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> readAllOrders() {
        return this.orderRepository.findAll()
                .stream()
                .map(order -> this.orderMapper.toResponse(order))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse readDashboardSummary() {
        var metrics = Optional.ofNullable(this.orderRepository.getDashboardMetrics())
                .orElse(new DashboardMetricsProjection(BigDecimal.ZERO, 0L, 0L, 0L, 0L));

        List<OrderResponse> recentOrders = this.orderRepository
                .findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(order -> this.orderMapper.toResponse(order))
                .toList();

        return new DashboardSummaryResponse(
                metrics.totalRevenue() != null ? metrics.totalRevenue() : BigDecimal.ZERO,
                metrics.totalOrders(),
                metrics.paidOrders(),
                metrics.pendingOrders(),
                metrics.cancelledOrders(),
                recentOrders
        );
    }

    private String generateUniqueOrderCode() {
        String code;
        do {
            int randomNum = 10000000 + RANDOM.nextInt(90000000);
            code = "ORD" + randomNum;
        } while (this.orderRepository.existsByOrderCode(code));
        return code;
    }

}
