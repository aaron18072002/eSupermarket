package com.coding.mapper;

import com.coding.dto.request.CreateInventoryRequest;
import com.coding.dto.response.InventoryResponse;
import com.coding.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quantityReserved", constant = "0")
    Inventory toEntity(CreateInventoryRequest request);

    @Mapping(target = "stockStatus", expression = "java(inventory.getStockStatus())")
    InventoryResponse toResponse(Inventory inventory);

}
