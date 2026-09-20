package com.coding.controller;

import com.coding.dto.request.CreateCategoryRequest;
import com.coding.dto.request.UpdateCategoryRequest;
import com.coding.dto.response.ApiResponse;
import com.coding.dto.response.CategoryResponse;
import com.coding.service.ICategoryService;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = this.categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CategoryResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Category created successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> readCategoryById(
            @PathVariable UUID id) {
        CategoryResponse response = this.categoryService.readCategoryById(id);
        return ResponseEntity.ok(
                ApiResponse.<CategoryResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Category retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> readAllCategories() {
        List<CategoryResponse> response = this.categoryService.readAllCategories();
        return ResponseEntity.ok(
                ApiResponse.<List<CategoryResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Categories retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/roots")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> readRootCategories() {
        List<CategoryResponse> response = this.categoryService.readRootCategories();
        return ResponseEntity.ok(
                ApiResponse.<List<CategoryResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Root categories retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}/subcategories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> readSubcategories(
            @PathVariable UUID id) {
        List<CategoryResponse> response = this.categoryService.readSubcategories(id);
        return ResponseEntity.ok(
                ApiResponse.<List<CategoryResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Subcategories retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryResponse response = this.categoryService.updateCategory(id, request);
        return ResponseEntity.ok(
                ApiResponse.<CategoryResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Category updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategoryById(
            @PathVariable UUID id) {
        this.categoryService.deleteCategoryById(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Category deleted successfully")
                        .build()
        );
    }

}
