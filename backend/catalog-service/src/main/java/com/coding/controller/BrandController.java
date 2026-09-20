package com.coding.controller;

import com.coding.dto.request.CreateBrandRequest;
import com.coding.dto.request.UpdateBrandRequest;
import com.coding.dto.response.ApiResponse;
import com.coding.dto.response.BrandResponse;
import com.coding.service.IBrandService;
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
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final IBrandService brandService;

    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(
            @Valid @RequestBody CreateBrandRequest request) {
        BrandResponse response = this.brandService.createBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<BrandResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Brand created successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> readBrandById(
            @PathVariable UUID id) {
        BrandResponse response = this.brandService.readBrandById(id);
        return ResponseEntity.ok(
                ApiResponse.<BrandResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Brand retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> readAllBrands() {
        List<BrandResponse> response = this.brandService.readAllBrands();
        return ResponseEntity.ok(
                ApiResponse.<List<BrandResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Brands retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBrandRequest request) {
        BrandResponse response = this.brandService.updateBrand(id, request);
        return ResponseEntity.ok(
                ApiResponse.<BrandResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Brand updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrandById(
            @PathVariable UUID id) {
        this.brandService.deleteBrandById(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Brand deleted successfully")
                        .build()
        );
    }

}
