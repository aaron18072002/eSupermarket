package com.coding.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PRODUCT_ATTRIBUTES")
public class ProductAttribute extends BaseEntity {

    /**
     * Unique identifier for the product attribute record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Reference to the product owning this attribute.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Key or name of the attribute (e.g., "Origin", "Storage Temperature", "Nutritional Value").
     */
    @Column(name = "attr_key", nullable = false)
    private String attrKey;

    /**
     * Value of the attribute (e.g., "Germany", "Keep Refrigerated (2-6°C)").
     */
    @Column(name = "attr_value", nullable = false)
    private String attrValue;

}