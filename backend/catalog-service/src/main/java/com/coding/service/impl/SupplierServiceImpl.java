package com.coding.service.impl;

import com.coding.dto.request.CreateSupplierRequest;
import com.coding.dto.request.UpdateSupplierRequest;
import com.coding.dto.response.SupplierResponse;
import com.coding.exception.DuplicateResourceException;
import com.coding.exception.ResourceNotFoundException;
import com.coding.mapper.SupplierMapper;
import com.coding.model.Supplier;
import com.coding.repository.SupplierRepository;
import com.coding.service.ISupplierService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class SupplierServiceImpl implements ISupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public SupplierResponse createSupplier(CreateSupplierRequest request) {
        if (this.supplierRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Supplier already exists with name: " + request.name());
        }

        Supplier supplier = this.supplierMapper.toEntity(request);
        Supplier savedSupplier = this.supplierRepository.save(supplier);
        return this.supplierMapper.toResponse(savedSupplier);
    }

    @Override
    public SupplierResponse readSupplierById(UUID supplierId) {
        return this.supplierRepository.findById(supplierId)
                .map(this.supplierMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + supplierId));
    }

    @Override
    public List<SupplierResponse> readAllSuppliers() {
        return this.supplierRepository.findAll()
                .stream()
                .map(this.supplierMapper::toResponse)
                .toList();
    }

    @Override
    public SupplierResponse updateSupplier(UUID supplierId, UpdateSupplierRequest request) {
        Supplier supplier = this.supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + supplierId));

        if (!supplier.getName().equalsIgnoreCase(request.name()) && this.supplierRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Supplier already exists with name: " + request.name());
        }

        this.supplierMapper.updateSupplierFromRequest(request, supplier);
        Supplier updatedSupplier = this.supplierRepository.saveAndFlush(supplier);
        return this.supplierMapper.toResponse(updatedSupplier);
    }

    @Override
    public void deleteSupplierById(UUID supplierId) {
        if (!this.supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Cannot delete. Supplier not found with ID: " + supplierId);
        }
        this.supplierRepository.deleteById(supplierId);
    }

}
