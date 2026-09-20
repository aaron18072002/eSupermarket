package com.coding.mapper;

import com.coding.dto.request.CreateSupplierRequest;
import com.coding.dto.request.UpdateSupplierRequest;
import com.coding.dto.response.SupplierResponse;
import com.coding.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    @Mapping(target = "id", ignore = true)
    Supplier toEntity(CreateSupplierRequest request);

    SupplierResponse toResponse(Supplier supplier);

    @Mapping(target = "id", ignore = true)
    void updateSupplierFromRequest(UpdateSupplierRequest request, @MappingTarget Supplier supplier);

}
