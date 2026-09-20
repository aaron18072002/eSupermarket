package com.coding.mapper;

import com.coding.dto.request.CreateProductRequest;
import com.coding.dto.request.UpdateProductRequest;
import com.coding.dto.response.ProductAttributeResponse;
import com.coding.dto.response.ProductResponse;
import com.coding.dto.response.TagResponse;
import com.coding.model.Product;
import com.coding.model.ProductAttribute;
import com.coding.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    Product toEntity(CreateProductRequest request);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "brandId", source = "brand.id")
    @Mapping(target = "brandName", source = "brand.name")
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.name")
    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateProductFromRequest(UpdateProductRequest request, @MappingTarget Product product);

    TagResponse toTagResponse(Tag tag);

    ProductAttributeResponse toAttributeResponse(ProductAttribute attribute);

}
