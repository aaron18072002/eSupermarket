package com.coding.service;

import com.coding.dto.request.CreateInventoryRequest;
import com.coding.dto.request.DeductStockRequest;
import com.coding.dto.response.InventoryResponse;
import com.coding.dto.response.StockSummaryProjection;
import com.coding.exception.DuplicateResourceException;
import com.coding.exception.InsufficientStockException;
import com.coding.mapper.InventoryMapper;
import com.coding.model.Inventory;
import com.coding.model.StockStatus;
import com.coding.repository.InventoryRepository;
import com.coding.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        this.inventoryService = new InventoryServiceImpl(inventoryRepository, inventoryMapper);
    }

    @Test
    @DisplayName("Should create inventory record successfully")
    void testCreateInventorySuccess() {
        UUID productId = UUID.randomUUID();
        CreateInventoryRequest request = new CreateInventoryRequest(
                productId,
                "SKU-MILK-001",
                "Fresh Whole Milk 1L",
                "Aisle 3, Shelf B",
                100,
                15
        );

        Inventory entity = Inventory.builder()
                .productId(productId)
                .productSku("SKU-MILK-001")
                .productName("Fresh Whole Milk 1L")
                .quantityAvailable(100)
                .safetyStockLevel(15)
                .build();

        InventoryResponse response = new InventoryResponse(
                UUID.randomUUID(),
                productId,
                "SKU-MILK-001",
                "Fresh Whole Milk 1L",
                "Aisle 3, Shelf B",
                100,
                0,
                15,
                StockStatus.IN_STOCK,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(inventoryRepository.existsByProductSku(request.productSku())).thenReturn(false);
        when(inventoryRepository.existsByProductId(request.productId())).thenReturn(false);
        when(inventoryMapper.toEntity(request)).thenReturn(entity);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(entity);
        when(inventoryMapper.toResponse(entity)).thenReturn(response);

        InventoryResponse result = inventoryService.createInventory(request);

        assertNotNull(result);
        assertEquals("SKU-MILK-001", result.productSku());
        assertEquals(100, result.quantityAvailable());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when SKU already exists")
    void testCreateInventoryDuplicateSku() {
        CreateInventoryRequest request = new CreateInventoryRequest(
                UUID.randomUUID(),
                "SKU-EXISTING",
                "Product",
                "Warehouse",
                10,
                5
        );

        when(inventoryRepository.existsByProductSku("SKU-EXISTING")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> inventoryService.createInventory(request));
    }

    @Test
    @DisplayName("Should retrieve inventory dashboard summary correctly")
    void testReadDashboardSummary() {
        StockSummaryProjection projection = new StockSummaryProjection(50L, 40L, 8L, 2L);
        when(inventoryRepository.getStockSummary()).thenReturn(projection);
        when(inventoryRepository.findLowStockInventories()).thenReturn(List.of());

        var summary = inventoryService.readDashboardSummary();

        assertNotNull(summary);
        assertEquals(50L, summary.totalTrackedItems());
        assertEquals(40L, summary.inStockCount());
        assertEquals(8L, summary.lowStockCount());
        assertEquals(2L, summary.outOfStockCount());
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when deducting more than available")
    void testDeductStockInsufficient() {
        UUID productId = UUID.randomUUID();
        DeductStockRequest request = new DeductStockRequest(productId, 50);

        Inventory inventory = Inventory.builder()
                .productId(productId)
                .productName("Sample Item")
                .quantityAvailable(20)
                .build();

        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));

        assertThrows(InsufficientStockException.class, () -> inventoryService.deductStock(request));
    }

}
