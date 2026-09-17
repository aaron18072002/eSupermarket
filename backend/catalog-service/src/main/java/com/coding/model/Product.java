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
import jakarta.persistence.Index;
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
@Table(
        name = "PRODUCTS",
        indexes = {
                @Index(name = "idx_product_sku", columnList = "sku", unique = true),
                @Index(name = "idx_product_slug", columnList = "slug", unique = true),
                @Index(name = "idx_product_barcode", columnList = "barcode"),
                @Index(name = "idx_product_category_id", columnList = "category_id"),
                @Index(name = "idx_product_brand_id", columnList = "brand_id"),
                @Index(name = "idx_product_supplier_id", columnList = "supplier_id"),
                @Index(name = "idx_product_group_id", columnList = "group_id")
        }
)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @Column(name = "barcode", unique = true)
    private String barcode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "unit")
    private String unit;

    @Column(name = "package_size")
    private String packageSize;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private ProductGroup group;

    // Many-to-Many mapping for PRODUCT_TAGS table
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "PRODUCT_TAGS",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            indexes = {
                    @Index(name = "idx_product_tags_product_id", columnList = "product_id"),
                    @Index(name = "idx_product_tags_tag_id", columnList = "tag_id")
            }
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    // One-to-Many mapping for PRODUCT_ATTRIBUTES table
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductAttribute> attributes = new HashSet<>();

    // ElementCollection for PRODUCT_IMAGES since it lacks a dedicated primary key in the diagram
    @ElementCollection
    @CollectionTable(
            name = "PRODUCT_IMAGES",
            joinColumns = @JoinColumn(name = "product_id"),
            indexes = {
                    @Index(name = "idx_product_images_product_id", columnList = "product_id")
            }
    )
    @Column(name = "image_url", nullable = false)
    @Builder.Default
    private Set<String> imageUrls = new HashSet<>();

    public void addAttribute(ProductAttribute attribute) {
        if (attribute != null) {
            this.attributes.add(attribute);
            attribute.setProduct(this);
        }
    }

    public void removeAttribute(ProductAttribute attribute) {
        if (attribute != null) {
            this.attributes.remove(attribute);
            attribute.setProduct(null);
        }
    }

    public void addTag(Tag tag) {
        if (tag != null) {
            this.tags.add(tag);
        }
    }

    public void removeTag(Tag tag) {
        if (tag != null) {
            this.tags.remove(tag);
        }
    }

    public void addImageUrl(String imageUrl) {
        if (imageUrl != null && !imageUrl.isBlank()) {
            this.imageUrls.add(imageUrl);
        }
    }

    public void removeImageUrl(String imageUrl) {
        if (imageUrl != null) {
            this.imageUrls.remove(imageUrl);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id != null && id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}