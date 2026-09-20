package com.coding.service.impl;

import com.coding.dto.request.CreateProductGroupRequest;
import com.coding.dto.request.UpdateProductGroupRequest;
import com.coding.dto.response.ProductGroupResponse;
import com.coding.exception.DuplicateResourceException;
import com.coding.exception.ResourceNotFoundException;
import com.coding.mapper.ProductGroupMapper;
import com.coding.model.ProductGroup;
import com.coding.repository.ProductGroupRepository;
import com.coding.service.IProductGroupService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class ProductGroupServiceImpl implements IProductGroupService {

    private final ProductGroupRepository productGroupRepository;
    private final ProductGroupMapper productGroupMapper;

    @Override
    public ProductGroupResponse createProductGroup(CreateProductGroupRequest request) {
        if (this.productGroupRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Product group already exists with name: " + request.name());
        }

        ProductGroup productGroup = this.productGroupMapper.toEntity(request);
        ProductGroup savedGroup = this.productGroupRepository.save(productGroup);
        return this.productGroupMapper.toResponse(savedGroup);
    }

    @Override
    public ProductGroupResponse readProductGroupById(UUID groupId) {
        return this.productGroupRepository.findById(groupId)
                .map(this.productGroupMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product group not found with ID: " + groupId));
    }

    @Override
    public List<ProductGroupResponse> readAllProductGroups() {
        return this.productGroupRepository.findAll()
                .stream()
                .map(this.productGroupMapper::toResponse)
                .toList();
    }

    @Override
    public ProductGroupResponse updateProductGroup(UUID groupId, UpdateProductGroupRequest request) {
        ProductGroup productGroup = this.productGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Product group not found with ID: " + groupId));

        if (!productGroup.getName().equalsIgnoreCase(request.name()) && this.productGroupRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Product group already exists with name: " + request.name());
        }

        this.productGroupMapper.updateProductGroupFromRequest(request, productGroup);
        ProductGroup updatedGroup = this.productGroupRepository.saveAndFlush(productGroup);
        return this.productGroupMapper.toResponse(updatedGroup);
    }

    @Override
    public void deleteProductGroupById(UUID groupId) {
        if (!this.productGroupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Cannot delete. Product group not found with ID: " + groupId);
        }
        this.productGroupRepository.deleteById(groupId);
    }

}
