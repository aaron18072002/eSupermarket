package com.coding.mapper;

import com.coding.dto.request.CreateProductGroupRequest;
import com.coding.dto.request.UpdateProductGroupRequest;
import com.coding.dto.response.ProductGroupResponse;
import com.coding.model.ProductGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductGroupMapper {

    @Mapping(target = "id", ignore = true)
    ProductGroup toEntity(CreateProductGroupRequest request);

    ProductGroupResponse toResponse(ProductGroup productGroup);

    @Mapping(target = "id", ignore = true)
    void updateProductGroupFromRequest(UpdateProductGroupRequest request, @MappingTarget ProductGroup productGroup);

}
