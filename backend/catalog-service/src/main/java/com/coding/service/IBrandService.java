package com.coding.service;

import com.coding.dto.request.CreateBrandRequest;
import com.coding.dto.request.UpdateBrandRequest;
import com.coding.dto.response.BrandResponse;

import java.util.List;
import java.util.UUID;

public interface IBrandService {

    BrandResponse createBrand(CreateBrandRequest request);

    BrandResponse readBrandById(UUID brandId);

    List<BrandResponse> readAllBrands();

    BrandResponse updateBrand(UUID brandId, UpdateBrandRequest request);

    void deleteBrandById(UUID brandId);

}
