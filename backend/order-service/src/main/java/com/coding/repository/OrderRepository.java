package com.coding.repository;

import com.coding.model.Order;
import com.coding.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByOrderCode(String orderCode);

    boolean existsByOrderCode(String orderCode);

    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Order> findTop10ByOrderByCreatedAtDesc();

    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = com.coding.model.OrderStatus.PAID OR o.status = com.coding.model.OrderStatus.PROCESSING OR o.status = com.coding.model.OrderStatus.DELIVERED")
    BigDecimal sumTotalRevenue();

    @Query("""
        SELECT new com.coding.dto.response.DashboardMetricsProjection(
            COALESCE(SUM(CASE WHEN o.status = com.coding.model.OrderStatus.PAID OR o.status = com.coding.model.OrderStatus.PROCESSING OR o.status = com.coding.model.OrderStatus.DELIVERED THEN o.totalAmount ELSE 0 END), 0),
            COUNT(o),
            COUNT(CASE WHEN o.status = com.coding.model.OrderStatus.PAID THEN 1 END),
            COUNT(CASE WHEN o.status = com.coding.model.OrderStatus.PENDING_PAYMENT THEN 1 END),
            COUNT(CASE WHEN o.status = com.coding.model.OrderStatus.CANCELLED THEN 1 END)
        )
        FROM Order o
    """)
    com.coding.dto.response.DashboardMetricsProjection getDashboardMetrics();

}
