package com.coding.service.impl;

import com.coding.dto.request.CreateOrderRequest;
import com.coding.dto.response.DashboardSummaryResponse;
import com.coding.dto.response.OrderResponse;
import com.coding.dto.webhook.SepayWebhookPayload;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Value("${sepay.bank-account}")
    private String bankAccount;

    @Value("${sepay.bank-name}")
    private String bankName;

    @Value("${sepay.account-holder}")
    private String accountHolder;

    @Value("${sepay.qr-template}")
    private String qrTemplate;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Pattern ORDER_CODE_PATTERN =
            Pattern.compile("ORD[0-9]{8}", Pattern.CASE_INSENSITIVE);

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

        // Set status and payment details
        if (request.paymentMethod() == PaymentMethod.COD) {
            order.setStatus(OrderStatus.PROCESSING);
        } else {
            order.setStatus(OrderStatus.PENDING_PAYMENT);
            String qrUrl = buildVietQrUrl(orderCode, totalAmount);
            order.setPaymentQrUrl(qrUrl);
        }

        Order savedOrder = this.orderRepository.save(order);
        log.info("Created new order [{}] with total amount: {} VND", orderCode, totalAmount);
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
                .orElse(new com.coding.dto.response.DashboardMetricsProjection(BigDecimal.ZERO, 0L, 0L, 0L, 0L));

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

    @Override
    public boolean processSepayWebhook(SepayWebhookPayload payload) {
        String content = payload.getContent();
        if (content == null || content.isBlank()) {
            content = payload.getDescription();
        }

        if (content == null) {
            log.warn("SePay webhook received without content or description");
            return false;
        }

        // Extract order code from transfer content
        String orderCode = extractOrderCode(content);
        if (orderCode == null) {
            log.warn("Could not find matching order code in SePay transfer content: '{}'",
                    content);
            return false;
        }

        Optional<Order> orderOpt = this.orderRepository.findByOrderCode(orderCode);
        if (orderOpt.isEmpty()) {
            log.warn("No order found in database matching code [{}] from SePay content '{}'",
                    orderCode, content);
            return false;
        }

        Order order = orderOpt.get();

        // Idempotency: if already paid, return true without re-processing
        if (order.getStatus() == OrderStatus.PAID) {
            log.info("Order [{}] is already marked as PAID. Skipping duplicate webhook.",
                    orderCode);
            return true;
        }

        // Validate transfer amount matches or exceeds order total
        BigDecimal transferAmount = payload.getTransferAmount();
        if (transferAmount == null || transferAmount.compareTo(order.getTotalAmount()) < 0) {
            log.warn("Insufficient payment for order [{}]: expected {} but received {}",
                    orderCode, order.getTotalAmount(), transferAmount);
            return false;
        }

        // Mark as PAID
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        this.orderRepository.save(order);

        log.info("Successfully processed SePay payment for order [{}], amount: {} VND",
                orderCode, transferAmount);
        return true;
    }

    private String generateUniqueOrderCode() {
        String code;
        do {
            int randomNum = 10000000 + RANDOM.nextInt(90000000);
            code = "ORD" + randomNum;
        } while (this.orderRepository.existsByOrderCode(code));
        return code;
    }

    private String buildVietQrUrl(String orderCode, BigDecimal amount) {
        String encodedOrderCode = URLEncoder.encode(orderCode, StandardCharsets.UTF_8);
        String encodedHolder = URLEncoder.encode(this.accountHolder, StandardCharsets.UTF_8).replace("+", "%20");
        long roundedAmount = amount.longValue();
        return String.format("%s?bank=%s&acc=%s&template=compact&amount=%d&des=%s&showinfo=true&holder=%s",
                this.qrTemplate, this.bankName, this.bankAccount, roundedAmount, encodedOrderCode, encodedHolder);
    }

    private String extractOrderCode(String text) {
        Matcher matcher = ORDER_CODE_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group().toUpperCase();
        }
        return null;
    }

}
