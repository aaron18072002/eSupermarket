package com.coding.service;

import com.coding.dto.request.CreateSupplierRequest;
import com.coding.dto.request.UpdateSupplierRequest;
import com.coding.dto.response.SupplierResponse;

import java.util.List;
import java.util.UUID;

public interface ISupplierService {

    SupplierResponse createSupplier(CreateSupplierRequest request);

    SupplierResponse readSupplierById(UUID supplierId);

    List<SupplierResponse> readAllSuppliers();

    SupplierResponse updateSupplier(UUID supplierId, UpdateSupplierRequest request);

    void deleteSupplierById(UUID supplierId);

}
