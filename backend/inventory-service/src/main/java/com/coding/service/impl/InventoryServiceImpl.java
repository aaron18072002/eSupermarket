package com.coding.service.impl;

import com.coding.dto.request.CreateInventoryRequest;
import com.coding.dto.request.DeductStockRequest;
import com.coding.dto.request.UpdateStockRequest;
import com.coding.dto.response.InventoryDashboardSummaryResponse;
import com.coding.dto.response.InventoryResponse;
import com.coding.dto.response.StockSummaryProjection;
import com.coding.exception.DuplicateResourceException;
import com.coding.exception.InsufficientStockException;
import com.coding.exception.ResourceNotFoundException;
import com.coding.mapper.InventoryMapper;
import com.coding.model.Inventory;
import com.coding.repository.InventoryRepository;
import com.coding.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements IInventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    public InventoryResponse createInventory(CreateInventoryRequest request) {
        if (this.inventoryRepository.existsByProductSku(request.productSku())) {
            throw new DuplicateResourceException(
                    "Inventory already exists for product SKU: " + request.productSku()
            );
        }
        if (this.inventoryRepository.existsByProductId(request.productId())) {
            throw new DuplicateResourceException(
                    "Inventory already exists for product ID: " + request.productId()
            );
        }

        Inventory inventory = this.inventoryMapper.toEntity(request);
        if (inventory.getSafetyStockLevel() == null) {
            inventory.setSafetyStockLevel(10);
        }

        Inventory savedInventory = this.inventoryRepository.save(inventory);
        log.info("Created inventory record for SKU: [{}] with initial stock: {}",
                savedInventory.getProductSku(), savedInventory.getQuantityAvailable());
        return this.inventoryMapper.toResponse(savedInventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse readInventoryById(UUID id) {
        Inventory inventory = this.inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with ID: " + id));
        return this.inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse readInventoryByProductId(UUID productId) {
        Inventory inventory = this.inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product ID: " + productId
                ));
        return this.inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse readInventoryBySku(String sku) {
        Inventory inventory = this.inventoryRepository.findByProductSku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for SKU: " + sku));
        return this.inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> readAllInventories() {
        return this.inventoryRepository.findAll()
                .stream()
                .map(inv -> this.inventoryMapper.toResponse(inv))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> readLowStockInventories() {
        return this.inventoryRepository.findLowStockInventories()
                .stream()
                .map(inv -> this.inventoryMapper.toResponse(inv))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryDashboardSummaryResponse readDashboardSummary() {
        StockSummaryProjection summary = Optional.ofNullable(
                this.inventoryRepository.getStockSummary()
        ).orElse(new StockSummaryProjection(0L, 0L, 0L, 0L));

        List<InventoryResponse> lowStockAlerts = this.inventoryRepository.findLowStockInventories()
                .stream()
                .map(inv -> this.inventoryMapper.toResponse(inv))
                .toList();

        return new InventoryDashboardSummaryResponse(
                summary.totalItems(),
                summary.inStockItems(),
                summary.lowStockItems(),
                summary.outOfStockItems(),
                lowStockAlerts
        );
    }

    @Override
    public InventoryResponse updateStock(UUID id, UpdateStockRequest request) {
        Inventory inventory = this.inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with ID: " + id));

        inventory.setQuantityAvailable(request.quantityAvailable());
        if (request.quantityReserved() != null) {
            inventory.setQuantityReserved(request.quantityReserved());
        }
        if (request.safetyStockLevel() != null) {
            inventory.setSafetyStockLevel(request.safetyStockLevel());
        }
        if (request.warehouseLocation() != null) {
            inventory.setWarehouseLocation(request.warehouseLocation());
        }

        Inventory updated = this.inventoryRepository.save(inventory);
        log.info("Updated stock for SKU: [{}], new available: {}",
                updated.getProductSku(), updated.getQuantityAvailable());
        return this.inventoryMapper.toResponse(updated);
    }

    @Override
    public InventoryResponse deductStock(DeductStockRequest request) {
        Inventory inventory = this.inventoryRepository.findByProductId(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory not found for product ID: " + request.productId()
                ));

        if (inventory.getQuantityAvailable() < request.quantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock for product [" + inventory.getProductName()
                            + "]. Available: " + inventory.getQuantityAvailable()
                            + ", requested: " + request.quantity()
            );
        }

        inventory.setQuantityAvailable(inventory.getQuantityAvailable() - request.quantity());
        Inventory updated = this.inventoryRepository.save(inventory);
        log.info("Deducted {} units from SKU [{}], remaining: {}",
                request.quantity(), updated.getProductSku(), updated.getQuantityAvailable());
        return this.inventoryMapper.toResponse(updated);
    }

    @Override
    public void deleteInventoryById(UUID id) {
        Inventory inventory = this.inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with ID: " + id));
        this.inventoryRepository.delete(inventory);
        log.info("Deleted inventory record for SKU: [{}]", inventory.getProductSku());
    }

}
