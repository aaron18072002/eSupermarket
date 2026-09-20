package com.coding.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PRODUCTS")
public class Product extends BaseEntity {

    /**
     * Unique identifier for the product.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Stock Keeping Unit (SKU) - internal unique code for inventory and catalog tracking.
     */
    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    /**
     * Standardized scannable barcode (e.g., EAN-13, UPC) for POS checkout and warehouse scanning.
     */
    @Column(name = "barcode", unique = true)
    private String barcode;

    /**
     * Display name of the product.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Retail selling price charged to customers.
     */
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    /**
     * Quantity or net weight/volume of the product package (e.g., 500 for 500g, 1.5 for 1.5L).
     */
    @Column(name = "package_size", precision = 10, scale = 3)
    private BigDecimal packageSize;

    /**
     * Unit of measurement for the package (e.g., "G", "KG", "ML", "L", "PIECE").
     */
    @Column(name = "unit_of_measure", length = 20)
    private String unitOfMeasure;

    /**
     * Category classification of the product.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * Brand or manufacturer of the product.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    /**
     * Supplier providing the product.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    /**
     * Product group for organizing variants or related products.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private ProductGroup group;

    /**
     * Tags associated with the product for filtering, promotions, and search.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "PRODUCT_TAGS",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    /**
     * Dynamic key-value attributes (e.g., organic, gluten-free, nutrition facts).
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductAttribute> attributes = new HashSet<>();

    /**
     * Collection of image URLs displaying the product in the storefront.
     */
    @ElementCollection
    @CollectionTable(
            name = "PRODUCT_IMAGES",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "image_url", nullable = false)
    @Builder.Default
    private Set<String> imageUrls = new HashSet<>();

}