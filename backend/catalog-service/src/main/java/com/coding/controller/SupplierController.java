package com.coding.controller;

import com.coding.dto.request.CreateSupplierRequest;
import com.coding.dto.request.UpdateSupplierRequest;
import com.coding.dto.response.ApiResponse;
import com.coding.dto.response.SupplierResponse;
import com.coding.service.ISupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Supplier", description = "Endpoints for managing product suppliers")
@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final ISupplierService supplierService;

    @Operation(summary = "Create a new supplier")
    @PostMapping
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(
            @Valid @RequestBody CreateSupplierRequest request) {
        SupplierResponse response = this.supplierService.createSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<SupplierResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Supplier created successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get supplier by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> readSupplierById(
            @PathVariable UUID id) {
        SupplierResponse response = this.supplierService.readSupplierById(id);
        return ResponseEntity.ok(
                ApiResponse.<SupplierResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Supplier retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Get all suppliers")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> readAllSuppliers() {
        List<SupplierResponse> response = this.supplierService.readAllSuppliers();
        return ResponseEntity.ok(
                ApiResponse.<List<SupplierResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Suppliers retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Update supplier by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSupplierRequest request) {
        SupplierResponse response = this.supplierService.updateSupplier(id, request);
        return ResponseEntity.ok(
                ApiResponse.<SupplierResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Supplier updated successfully")
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Delete supplier by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplierById(
            @PathVariable UUID id) {
        this.supplierService.deleteSupplierById(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Supplier deleted successfully")
                        .build()
        );
    }

}
