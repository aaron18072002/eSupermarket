package com.coding.service;

import com.coding.dto.request.CreateProductRequest;
import com.coding.dto.request.UpdateProductRequest;
import com.coding.dto.response.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface IProductService {

    /**
     * Creates a new product with all its associations.
     */
    ProductResponse createProduct(CreateProductRequest request);

    /**
     * Reads a product by its unique identifier.
     */
    ProductResponse readProductById(UUID productId);

    /**
     * Reads a product by its unique SKU.
     */
    ProductResponse readProductBySku(String sku);

    /**
     * Reads a product by its barcode.
     */
    ProductResponse readProductByBarcode(String barcode);

    /**
     * Reads all products across the catalog.
     */
    List<ProductResponse> readAllProducts();

    /**
     * Reads products belonging to a specific category.
     */
    List<ProductResponse> readProductsByCategory(UUID categoryId);

    /**
     * Reads products belonging to a specific brand.
     */
    List<ProductResponse> readProductsByBrand(UUID brandId);

    /**
     * Searches products by keyword in name, SKU, or barcode.
     */
    List<ProductResponse> searchProducts(String query);

    /**
     * Updates an existing product and its relationships.
     */
    ProductResponse updateProduct(UUID productId, UpdateProductRequest request);

    /**
     * Deletes a product by its unique identifier.
     */
    void deleteProductById(UUID productId);

}
