package com.coding.service;

import com.coding.dto.request.CreateCategoryRequest;
import com.coding.dto.request.UpdateCategoryRequest;
import com.coding.dto.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface ICategoryService {

    /**
     * Creates a new product category.
     */
    CategoryResponse createCategory(CreateCategoryRequest request);

    /**
     * Reads a category by its unique identifier.
     */
    CategoryResponse readCategoryById(UUID categoryId);

    /**
     * Reads all categories in the system.
     */
    List<CategoryResponse> readAllCategories();

    /**
     * Reads all root/top-level categories (categories without parents).
     */
    List<CategoryResponse> readRootCategories();

    /**
     * Reads all subcategories under a specific parent category.
     */
    List<CategoryResponse> readSubcategories(UUID parentId);

    /**
     * Updates an existing category.
     */
    CategoryResponse updateCategory(UUID categoryId, UpdateCategoryRequest request);

    /**
     * Deletes a category by its unique identifier.
     */
    void deleteCategoryById(UUID categoryId);

}
