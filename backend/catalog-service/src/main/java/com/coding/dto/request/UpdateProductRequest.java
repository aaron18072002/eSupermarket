package com.coding.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record UpdateProductRequest(

        @NotBlank(message = "SKU is required")
        String sku,

        String barcode,

        @NotBlank(message = "Product name is required")
        String name,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        @DecimalMin(value = "0.0", inclusive = false, message = "Package size must be greater than 0")
        BigDecimal packageSize,

        String unitOfMeasure,

        @NotNull(message = "Category ID is required")
        UUID categoryId,

        UUID brandId,

        UUID supplierId,

        UUID groupId,

        Set<UUID> tagIds,

        Set<@Valid ProductAttributeRequest> attributes,

        Set<String> imageUrls

) {}
