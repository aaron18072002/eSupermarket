package com.coding.service.impl;

import com.coding.dto.request.CreateCategoryRequest;
import com.coding.dto.request.UpdateCategoryRequest;
import com.coding.dto.response.CategoryResponse;
import com.coding.exception.DuplicateResourceException;
import com.coding.exception.ResourceNotFoundException;
import com.coding.mapper.CategoryMapper;
import com.coding.model.Category;
import com.coding.repository.CategoryRepository;
import com.coding.service.ICategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        if (this.categoryRepository.existsByName(request.name())) {
            throw new DuplicateResourceException
                    ("Category already exists with name: " + request.name());
        }

        Category parent = null;
        if (request.parentId() != null) {
            parent = this.categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException
                            ("Parent category not found with ID: " + request.parentId()));
        }

        Category category = this.categoryMapper.toEntity(request);
        category.setParent(parent);

        Category savedCategory = this.categoryRepository.save(category);
        return this.categoryMapper.toResponse(savedCategory);
    }

    @Override
    public CategoryResponse readCategoryById(UUID categoryId) {
        return this.categoryRepository.findById(categoryId)
                .map(this.categoryMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Category not found with ID: " + categoryId));
    }

    @Override
    public List<CategoryResponse> readAllCategories() {
        return this.categoryRepository.findAll()
                .stream()
                .map(this.categoryMapper::toResponse)
                .toList();
    }

    @Override
    public List<CategoryResponse> readRootCategories() {
        return this.categoryRepository.findByParentIsNull()
                .stream()
                .map(this.categoryMapper::toResponse)
                .toList();
    }

    @Override
    public List<CategoryResponse> readSubcategories(UUID parentId) {
        if (!this.categoryRepository.existsById(parentId)) {
            throw new ResourceNotFoundException
                    ("Parent category not found with ID: " + parentId);
        }
        return this.categoryRepository.findByParentId(parentId)
                .stream()
                .map(this.categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse updateCategory(UUID categoryId, UpdateCategoryRequest request) {
        Category category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Category not found with ID: " + categoryId));

        if (!category.getName().equalsIgnoreCase(request.name())
                && this.categoryRepository.existsByName(request.name())) {
            throw new DuplicateResourceException
                    ("Category already exists with name: " + request.name());
        }

        if (request.parentId() != null && request.parentId().equals(categoryId)) {
            throw new IllegalArgumentException
                    ("A category cannot be its own parent.");
        }

        this.categoryMapper.updateCategoryFromRequest(request, category);

        Category parent = null;
        if (request.parentId() != null) {
            parent = this.categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException
                            ("Parent category not found with ID: " + request.parentId()));
        }
        category.setParent(parent);

        Category updatedCategory = this.categoryRepository.saveAndFlush(category);
        return this.categoryMapper.toResponse(updatedCategory);
    }

    @Override
    public void deleteCategoryById(UUID categoryId) {
        if (!this.categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException
                    ("Cannot delete. Category not found with ID: " + categoryId);
        }
        this.categoryRepository.deleteById(categoryId);
    }

}
