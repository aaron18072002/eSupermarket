package com.coding.service;

import com.coding.dto.request.CreateInventoryRequest;
import com.coding.dto.request.DeductStockRequest;
import com.coding.dto.request.UpdateStockRequest;
import com.coding.dto.response.InventoryDashboardSummaryResponse;
import com.coding.dto.response.InventoryResponse;

import java.util.List;
import java.util.UUID;

public interface IInventoryService {

    InventoryResponse createInventory(CreateInventoryRequest request);

    InventoryResponse readInventoryById(UUID id);

    InventoryResponse readInventoryByProductId(UUID productId);

    InventoryResponse readInventoryBySku(String sku);

    List<InventoryResponse> readAllInventories();

    List<InventoryResponse> readLowStockInventories();

    InventoryDashboardSummaryResponse readDashboardSummary();

    InventoryResponse updateStock(UUID id, UpdateStockRequest request);

    InventoryResponse deductStock(DeductStockRequest request);

    void deleteInventoryById(UUID id);

}
