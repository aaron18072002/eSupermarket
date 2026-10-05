package com.coding.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "inventories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_sku", unique = true, nullable = false, length = 100)
    private String productSku;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "warehouse_location", length = 150)
    private String warehouseLocation;

    @Column(name = "quantity_available", nullable = false)
    private Integer quantityAvailable;

    @Column(name = "quantity_reserved", nullable = false)
    @Builder.Default
    private Integer quantityReserved = 0;

    @Column(name = "safety_stock_level", nullable = false)
    @Builder.Default
    private Integer safetyStockLevel = 10;

    public StockStatus getStockStatus() {
        if (this.quantityAvailable == null || this.quantityAvailable <= 0) {
            return StockStatus.OUT_OF_STOCK;
        }
        if (this.safetyStockLevel != null && this.quantityAvailable <= this.safetyStockLevel) {
            return StockStatus.LOW_STOCK;
        }
        return StockStatus.IN_STOCK;
    }

}
