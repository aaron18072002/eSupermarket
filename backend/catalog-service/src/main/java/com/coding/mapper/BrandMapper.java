package com.coding.mapper;

import com.coding.dto.request.CreateBrandRequest;
import com.coding.dto.request.UpdateBrandRequest;
import com.coding.dto.response.BrandResponse;
import com.coding.model.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    @Mapping(target = "id", ignore = true)
    Brand toEntity(CreateBrandRequest request);

    BrandResponse toResponse(Brand brand);

    @Mapping(target = "id", ignore = true)
    void updateBrandFromRequest(UpdateBrandRequest request, @MappingTarget Brand brand);

}
