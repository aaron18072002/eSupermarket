package com.coding.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String sku,
        String barcode,
        String name,
        BigDecimal price,
        BigDecimal packageSize,
        String unitOfMeasure,
        UUID categoryId,
        String categoryName,
        UUID brandId,
        String brandName,
        UUID supplierId,
        String supplierName,
        UUID groupId,
        String groupName,
        Set<TagResponse> tags,
        Set<ProductAttributeResponse> attributes,
        Set<String> imageUrls,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
