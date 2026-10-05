package com.coding.repository;

import com.coding.dto.response.StockSummaryProjection;
import com.coding.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    Optional<Inventory> findByProductId(UUID productId);

    Optional<Inventory> findByProductSku(String productSku);

    boolean existsByProductSku(String productSku);

    boolean existsByProductId(UUID productId);

    @Query("SELECT i FROM Inventory i WHERE i.quantityAvailable <= i.safetyStockLevel ORDER BY i.quantityAvailable ASC")
    List<Inventory> findLowStockInventories();

    @Query("""
        SELECT new com.coding.dto.response.StockSummaryProjection(
            COUNT(i),
            COUNT(CASE WHEN i.quantityAvailable > i.safetyStockLevel THEN 1 END),
            COUNT(CASE WHEN i.quantityAvailable <= i.safetyStockLevel AND i.quantityAvailable > 0 THEN 1 END),
            COUNT(CASE WHEN i.quantityAvailable <= 0 THEN 1 END)
        )
        FROM Inventory i
    """)
    StockSummaryProjection getStockSummary();

}
