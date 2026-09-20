package com.coding.service;

import com.coding.dto.request.CreateProductGroupRequest;
import com.coding.dto.request.UpdateProductGroupRequest;
import com.coding.dto.response.ProductGroupResponse;

import java.util.List;
import java.util.UUID;

public interface IProductGroupService {

    ProductGroupResponse createProductGroup(CreateProductGroupRequest request);

    ProductGroupResponse readProductGroupById(UUID groupId);

    List<ProductGroupResponse> readAllProductGroups();

    ProductGroupResponse updateProductGroup(UUID groupId, UpdateProductGroupRequest request);

    void deleteProductGroupById(UUID groupId);

}
