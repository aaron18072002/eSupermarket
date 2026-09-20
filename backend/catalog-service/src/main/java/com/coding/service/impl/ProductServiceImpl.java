package com.coding.service.impl;

import com.coding.dto.request.CreateProductRequest;
import com.coding.dto.request.UpdateProductRequest;
import com.coding.dto.response.ProductResponse;
import com.coding.exception.DuplicateResourceException;
import com.coding.exception.ResourceNotFoundException;
import com.coding.mapper.ProductMapper;
import com.coding.model.Brand;
import com.coding.model.Category;
import com.coding.model.Product;
import com.coding.model.ProductAttribute;
import com.coding.model.ProductGroup;
import com.coding.model.Supplier;
import com.coding.model.Tag;
import com.coding.repository.BrandRepository;
import com.coding.repository.CategoryRepository;
import com.coding.repository.ProductGroupRepository;
import com.coding.repository.ProductRepository;
import com.coding.repository.SupplierRepository;
import com.coding.repository.TagRepository;
import com.coding.service.IProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SupplierRepository supplierRepository;
    private final ProductGroupRepository productGroupRepository;
    private final TagRepository tagRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        if (this.productRepository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("Product with SKU already exists: " + request.sku());
        }

        if (request.barcode() != null && !request.barcode().isBlank()
                && this.productRepository.existsByBarcode(request.barcode())) {
            throw new DuplicateResourceException("Product with barcode already exists: " + request.barcode());
        }

        Category category = this.categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.categoryId()));

        Brand brand = request.brandId() != null
                ? this.brandRepository.findById(request.brandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + request.brandId()))
                : null;

        Supplier supplier = request.supplierId() != null
                ? this.supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + request.supplierId()))
                : null;

        ProductGroup group = request.groupId() != null
                ? this.productGroupRepository.findById(request.groupId())
                .orElseThrow(() -> new ResourceNotFoundException("Product group not found with ID: " + request.groupId()))
                : null;

        Set<Tag> tags = (request.tagIds() != null && !request.tagIds().isEmpty())
                ? new HashSet<>(this.tagRepository.findAllById(request.tagIds()))
                : new HashSet<>();

        Product product = this.productMapper.toEntity(request);
        product.setCategory(category);
        product.setBrand(brand);
        product.setSupplier(supplier);
        product.setGroup(group);
        product.setTags(tags);
        product.setImageUrls(request.imageUrls() != null ? new HashSet<>(request.imageUrls()) : new HashSet<>());

        if (request.attributes() != null && !request.attributes().isEmpty()) {
            Set<ProductAttribute> attributes = request.attributes().stream()
                    .map(attrReq -> ProductAttribute.builder()
                            .product(product)
                            .attrKey(attrReq.attrKey())
                            .attrValue(attrReq.attrValue())
                            .build())
                    .collect(Collectors.toSet());
            product.setAttributes(attributes);
        }

        Product savedProduct = this.productRepository.save(product);
        return this.productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse readProductById(UUID productId) {
        return this.productRepository.findById(productId)
                .map(this.productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }

    @Override
    public ProductResponse readProductBySku(String sku) {
        return this.productRepository.findBySku(sku)
                .map(this.productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
    }

    @Override
    public ProductResponse readProductByBarcode(String barcode) {
        return this.productRepository.findByBarcode(barcode)
                .map(this.productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with barcode: " + barcode));
    }

    @Override
    public List<ProductResponse> readAllProducts() {
        return this.productRepository.findAll()
                .stream()
                .map(this.productMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> readProductsByCategory(UUID categoryId) {
        return this.productRepository.findByCategoryId(categoryId)
                .stream()
                .map(this.productMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> readProductsByBrand(UUID brandId) {
        return this.productRepository.findByBrandId(brandId)
                .stream()
                .map(this.productMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> searchProducts(String query) {
        return this.productRepository.searchProducts(query)
                .stream()
                .map(this.productMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponse updateProduct(UUID productId, UpdateProductRequest request) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (!product.getSku().equals(request.sku()) && this.productRepository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("Product with SKU already exists: " + request.sku());
        }

        if (request.barcode() != null && !request.barcode().isBlank()
                && !request.barcode().equals(product.getBarcode())
                && this.productRepository.existsByBarcode(request.barcode())) {
            throw new DuplicateResourceException("Product with barcode already exists: " + request.barcode());
        }

        this.productMapper.updateProductFromRequest(request, product);

        Category category = this.categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.categoryId()));
        product.setCategory(category);

        Brand brand = request.brandId() != null
                ? this.brandRepository.findById(request.brandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + request.brandId()))
                : null;
        product.setBrand(brand);

        Supplier supplier = request.supplierId() != null
                ? this.supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + request.supplierId()))
                : null;
        product.setSupplier(supplier);

        ProductGroup group = request.groupId() != null
                ? this.productGroupRepository.findById(request.groupId())
                .orElseThrow(() -> new ResourceNotFoundException("Product group not found with ID: " + request.groupId()))
                : null;
        product.setGroup(group);

        Set<Tag> tags = (request.tagIds() != null && !request.tagIds().isEmpty())
                ? new HashSet<>(this.tagRepository.findAllById(request.tagIds()))
                : new HashSet<>();
        product.setTags(tags);

        product.setImageUrls(request.imageUrls() != null ? new HashSet<>(request.imageUrls()) : new HashSet<>());

        product.getAttributes().clear();
        if (request.attributes() != null && !request.attributes().isEmpty()) {
            Set<ProductAttribute> attributes = request.attributes().stream()
                    .map(attrReq -> ProductAttribute.builder()
                            .product(product)
                            .attrKey(attrReq.attrKey())
                            .attrValue(attrReq.attrValue())
                            .build())
                    .collect(Collectors.toSet());
            product.getAttributes().addAll(attributes);
        }

        Product updatedProduct = this.productRepository.saveAndFlush(product);
        return this.productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProductById(UUID productId) {
        if (!this.productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Cannot delete. Product not found with ID: " + productId);
        }
        this.productRepository.deleteById(productId);
    }

}
