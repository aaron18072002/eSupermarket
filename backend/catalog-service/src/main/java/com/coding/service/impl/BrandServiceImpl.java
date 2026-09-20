package com.coding.service.impl;

import com.coding.dto.request.CreateBrandRequest;
import com.coding.dto.request.UpdateBrandRequest;
import com.coding.dto.response.BrandResponse;
import com.coding.exception.DuplicateResourceException;
import com.coding.exception.ResourceNotFoundException;
import com.coding.mapper.BrandMapper;
import com.coding.model.Brand;
import com.coding.repository.BrandRepository;
import com.coding.service.IBrandService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class BrandServiceImpl implements IBrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public BrandResponse createBrand(CreateBrandRequest request) {
        if (this.brandRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Brand already exists with name: " + request.name());
        }

        Brand brand = this.brandMapper.toEntity(request);
        Brand savedBrand = this.brandRepository.save(brand);
        return this.brandMapper.toResponse(savedBrand);
    }

    @Override
    public BrandResponse readBrandById(UUID brandId) {
        return this.brandRepository.findById(brandId)
                .map(this.brandMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + brandId));
    }

    @Override
    public List<BrandResponse> readAllBrands() {
        return this.brandRepository.findAll()
                .stream()
                .map(this.brandMapper::toResponse)
                .toList();
    }

    @Override
    public BrandResponse updateBrand(UUID brandId, UpdateBrandRequest request) {
        Brand brand = this.brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + brandId));

        if (!brand.getName().equalsIgnoreCase(request.name()) && this.brandRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Brand already exists with name: " + request.name());
        }

        this.brandMapper.updateBrandFromRequest(request, brand);
        Brand updatedBrand = this.brandRepository.saveAndFlush(brand);
        return this.brandMapper.toResponse(updatedBrand);
    }

    @Override
    public void deleteBrandById(UUID brandId) {
        if (!this.brandRepository.existsById(brandId)) {
            throw new ResourceNotFoundException("Cannot delete. Brand not found with ID: " + brandId);
        }
        this.brandRepository.deleteById(brandId);
    }

}
