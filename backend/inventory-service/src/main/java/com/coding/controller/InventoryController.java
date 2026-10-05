package com.coding.controller;

import com.coding.dto.request.CreateInventoryRequest;
import com.coding.dto.request.DeductStockRequest;
import com.coding.dto.request.UpdateStockRequest;
import com.coding.dto.response.ApiResponse;
import com.coding.dto.response.InventoryDashboardSummaryResponse;
import com.coding.dto.response.InventoryResponse;
import com.coding.security.JwtUtil;
import com.coding.service.IInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Inventory Management", description = "Endpoints for warehouse inventory tracking and stock adjustments")
@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final IInventoryService inventoryService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Create an initial inventory record for a product (Admin Only)")
    @PostMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> createInventory(
            @Valid @RequestBody CreateInventoryRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        this.jwtUtil.validateAdminRole(authHeader);
        InventoryResponse response = this.inventoryService.createInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<InventoryResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Inventory record created successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get inventory record by inventory ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponse>> readInventoryById(@PathVariable UUID id) {
        InventoryResponse response = this.inventoryService.readInventoryById(id);
        return ResponseEntity.ok(
                ApiResponse.<InventoryResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Inventory retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get inventory stock by product ID")
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<InventoryResponse>> readInventoryByProductId(
            @PathVariable UUID productId) {
        InventoryResponse response = this.inventoryService.readInventoryByProductId(productId);
        return ResponseEntity.ok(
                ApiResponse.<InventoryResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Product inventory retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get inventory stock by product SKU")
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<InventoryResponse>> readInventoryBySku(@PathVariable String sku) {
        InventoryResponse response = this.inventoryService.readInventoryBySku(sku);
        return ResponseEntity.ok(
                ApiResponse.<InventoryResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("SKU inventory retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get all inventory records")
    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> readAllInventories() {
        List<InventoryResponse> response = this.inventoryService.readAllInventories();
        return ResponseEntity.ok(
                ApiResponse.<List<InventoryResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("All inventories retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get all low stock inventory alerts (Admin Only)")
    @GetMapping("/alerts/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> readLowStockInventories(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        this.jwtUtil.validateAdminRole(authHeader);
        List<InventoryResponse> response = this.inventoryService.readLowStockInventories();
        return ResponseEntity.ok(
                ApiResponse.<List<InventoryResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Low stock alerts retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get inventory metrics for Admin Dashboard (Admin Only)")
    @GetMapping("/dashboard/summary")
    public ResponseEntity<ApiResponse<InventoryDashboardSummaryResponse>> readDashboardSummary(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        this.jwtUtil.validateAdminRole(authHeader);
        InventoryDashboardSummaryResponse response = this.inventoryService.readDashboardSummary();
        return ResponseEntity.ok(
                ApiResponse.<InventoryDashboardSummaryResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Inventory dashboard summary retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Update stock quantities and warehouse location (Admin Only)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponse>> updateStock(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStockRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        this.jwtUtil.validateAdminRole(authHeader);
        InventoryResponse response = this.inventoryService.updateStock(id, request);
        return ResponseEntity.ok(
                ApiResponse.<InventoryResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Stock updated successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Deduct available stock during checkout")
    @PostMapping("/deduct")
    public ResponseEntity<ApiResponse<InventoryResponse>> deductStock(
            @Valid @RequestBody DeductStockRequest request) {
        InventoryResponse response = this.inventoryService.deductStock(request);
        return ResponseEntity.ok(
                ApiResponse.<InventoryResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Stock deducted successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Delete inventory record by ID (Admin Only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInventoryById(
            @PathVariable UUID id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        this.jwtUtil.validateAdminRole(authHeader);
        this.inventoryService.deleteInventoryById(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Inventory deleted successfully")
                        .build()
        );
    }

}
