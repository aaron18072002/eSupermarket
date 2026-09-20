package com.coding.controller;

import com.coding.dto.request.CreateProductGroupRequest;
import com.coding.dto.request.UpdateProductGroupRequest;
import com.coding.dto.response.ApiResponse;
import com.coding.dto.response.ProductGroupResponse;
import com.coding.service.IProductGroupService;
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

@RestController
@RequestMapping("/api/v1/product-groups")
@RequiredArgsConstructor
public class ProductGroupController {

    private final IProductGroupService productGroupService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductGroupResponse>> createProductGroup(
            @Valid @RequestBody CreateProductGroupRequest request) {
        ProductGroupResponse response = this.productGroupService.createProductGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ProductGroupResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Product group created successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductGroupResponse>> readProductGroupById(
            @PathVariable UUID id) {
        ProductGroupResponse response = this.productGroupService.readProductGroupById(id);
        return ResponseEntity.ok(
                ApiResponse.<ProductGroupResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Product group retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductGroupResponse>>> readAllProductGroups() {
        List<ProductGroupResponse> response = this.productGroupService.readAllProductGroups();
        return ResponseEntity.ok(
                ApiResponse.<List<ProductGroupResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Product groups retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductGroupResponse>> updateProductGroup(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductGroupRequest request) {
        ProductGroupResponse response = this.productGroupService.updateProductGroup(id, request);
        return ResponseEntity.ok(
                ApiResponse.<ProductGroupResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Product group updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProductGroupById(
            @PathVariable UUID id) {
        this.productGroupService.deleteProductGroupById(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Product group deleted successfully")
                        .build()
        );
    }

}
