package com.coding.controller;

import com.coding.dto.request.CreateProductRequest;
import com.coding.dto.request.UpdateProductRequest;
import com.coding.dto.response.ApiResponse;
import com.coding.dto.response.ProductResponse;
import com.coding.service.IProductService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = this.productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ProductResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Product created successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> readProductById(
            @PathVariable UUID id) {
        ProductResponse response = this.productService.readProductById(id);
        return ResponseEntity.ok(
                ApiResponse.<ProductResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Product retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<ProductResponse>> readProductBySku(
            @PathVariable String sku) {
        ProductResponse response = this.productService.readProductBySku(sku);
        return ResponseEntity.ok(
                ApiResponse.<ProductResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Product retrieved successfully by SKU")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ApiResponse<ProductResponse>> readProductByBarcode(
            @PathVariable String barcode) {
        ProductResponse response = this.productService.readProductByBarcode(barcode);
        return ResponseEntity.ok(
                ApiResponse.<ProductResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Product retrieved successfully by barcode")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> readAllProducts() {
        List<ProductResponse> response = this.productService.readAllProducts();
        return ResponseEntity.ok(
                ApiResponse.<List<ProductResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Products retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> readProductsByCategory(
            @PathVariable UUID categoryId) {
        List<ProductResponse> response = this.productService.readProductsByCategory(categoryId);
        return ResponseEntity.ok(
                ApiResponse.<List<ProductResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Products retrieved successfully by category")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> readProductsByBrand(
            @PathVariable UUID brandId) {
        List<ProductResponse> response = this.productService.readProductsByBrand(brandId);
        return ResponseEntity.ok(
                ApiResponse.<List<ProductResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Products retrieved successfully by brand")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(
            @RequestParam String query) {
        List<ProductResponse> response = this.productService.searchProducts(query);
        return ResponseEntity.ok(
                ApiResponse.<List<ProductResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Products search completed successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductResponse response = this.productService.updateProduct(id, request);
        return ResponseEntity.ok(
                ApiResponse.<ProductResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Product updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProductById(
            @PathVariable UUID id) {
        this.productService.deleteProductById(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Product deleted successfully")
                        .build()
        );
    }

}
