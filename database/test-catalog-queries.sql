-- ==============================================================================
-- eSupermarket / Điện Máy Xanh - Catalog Service Inspection & Testing Queries
-- Based on entities in: backend/catalog-service/src/main/java/com/coding/model
-- Compatible with PostgreSQL 16 & DBeaver
-- ==============================================================================

-- ==============================================================================
-- QUERY 1: MASTER CATALOG VIEW (Clean 1-row-per-product with all relations joined)
-- Connects: Product, Category (Hierarchy), Brand, Supplier, Group, Tags, Attributes, Images
-- Displays price in VND format
-- ==============================================================================
SELECT 
    p.id AS product_id,
    p.sku,
    p.barcode,
    p.name AS product_name,
    TO_CHAR(p.price, 'FM999,999,999') || ' ₫' AS formatted_price_vnd,
    p.price AS raw_price,
    CONCAT(p.package_size, ' ', p.unit_of_measure) AS spec_summary,
    COALESCE(parent_cat.name || ' > ', '') || cat.name AS category_path,
    cat.name AS subcategory,
    COALESCE(parent_cat.name, 'Root') AS parent_category,
    b.name AS brand,
    COALESCE(b.country, '-') AS brand_origin,
    s.name AS supplier,
    COALESCE(pg.name, 'None') AS product_group,
    COALESCE(STRING_AGG(DISTINCT t.name, ', '), 'None') AS tags,
    COALESCE(STRING_AGG(DISTINCT pa.attr_key || ': ' || pa.attr_value, ' | '), 'None') AS spec_attributes,
    COALESCE(STRING_AGG(DISTINCT pi.image_url, ', '), 'None') AS images,
    p.created_at,
    p.updated_at
FROM PRODUCTS p
LEFT JOIN CATEGORIES cat ON p.category_id = cat.id
LEFT JOIN CATEGORIES parent_cat ON cat.parent_id = parent_cat.id
LEFT JOIN BRANDS b ON p.brand_id = b.id
LEFT JOIN SUPPLIERS s ON p.supplier_id = s.id
LEFT JOIN PRODUCT_GROUPS pg ON p.group_id = pg.id
LEFT JOIN PRODUCT_TAGS pt ON p.id = pt.product_id
LEFT JOIN TAGS t ON pt.tag_id = t.id
LEFT JOIN PRODUCT_ATTRIBUTES pa ON p.id = pa.product_id
LEFT JOIN PRODUCT_IMAGES pi ON p.id = pi.product_id
GROUP BY 
    p.id,
    p.sku,
    p.barcode,
    p.name,
    p.price,
    p.package_size,
    p.unit_of_measure,
    cat.name,
    parent_cat.name,
    b.name,
    b.country,
    s.name,
    pg.name,
    p.created_at,
    p.updated_at
ORDER BY p.sku;


-- ==============================================================================
-- QUERY 2: JSON VIEW (Simulates the Catalog Service /api/v1/products API Response)
-- ==============================================================================
SELECT 
    jsonb_pretty(
        jsonb_build_object(
            'id', p.id,
            'sku', p.sku,
            'barcode', p.barcode,
            'name', p.name,
            'price', p.price,
            'package', jsonb_build_object('size', p.package_size, 'unit', p.unit_of_measure),
            'category', jsonb_build_object(
                'id', cat.id,
                'name', cat.name,
                'parent', CASE WHEN parent_cat.id IS NOT NULL THEN jsonb_build_object('id', parent_cat.id, 'name', parent_cat.name) ELSE NULL END
            ),
            'brand', jsonb_build_object('id', b.id, 'name', b.name, 'country', b.country),
            'supplier', jsonb_build_object('id', s.id, 'name', s.name),
            'group', CASE WHEN pg.id IS NOT NULL THEN jsonb_build_object('id', pg.id, 'name', pg.name) ELSE NULL END,
            'tags', COALESCE(jsonb_agg(DISTINCT t.name) FILTER (WHERE t.name IS NOT NULL), '[]'::jsonb),
            'attributes', COALESCE(jsonb_object_agg(pa.attr_key, pa.attr_value) FILTER (WHERE pa.attr_key IS NOT NULL), '{}'::jsonb),
            'images', COALESCE(jsonb_agg(DISTINCT pi.image_url) FILTER (WHERE pi.image_url IS NOT NULL), '[]'::jsonb)
        )
    ) AS catalog_product_json
FROM PRODUCTS p
LEFT JOIN CATEGORIES cat ON p.category_id = cat.id
LEFT JOIN CATEGORIES parent_cat ON cat.parent_id = parent_cat.id
LEFT JOIN BRANDS b ON p.brand_id = b.id
LEFT JOIN SUPPLIERS s ON p.supplier_id = s.id
LEFT JOIN PRODUCT_GROUPS pg ON p.group_id = pg.id
LEFT JOIN PRODUCT_TAGS pt ON p.id = pt.product_id
LEFT JOIN TAGS t ON pt.tag_id = t.id
LEFT JOIN PRODUCT_ATTRIBUTES pa ON p.id = pa.product_id
LEFT JOIN PRODUCT_IMAGES pi ON p.id = pi.product_id
GROUP BY p.id, p.sku, p.barcode, p.name, p.price, p.package_size, p.unit_of_measure,
         cat.id, cat.name, parent_cat.id, parent_cat.name, b.id, b.name, b.country,
         s.id, s.name, pg.id, pg.name
ORDER BY p.sku;


-- ==============================================================================
-- QUERY 3: HIERARCHICAL CATEGORY NAVIGATION TREE (For Header & Megamenu)
-- ==============================================================================
SELECT 
    parent.id AS root_category_id,
    parent.name AS root_category_name,
    child.id AS subcategory_id,
    child.name AS subcategory_name,
    COUNT(p.id) AS product_count
FROM CATEGORIES parent
LEFT JOIN CATEGORIES child ON child.parent_id = parent.id
LEFT JOIN PRODUCTS p ON p.category_id = child.id
WHERE parent.parent_id IS NULL
GROUP BY parent.id, parent.name, child.id, child.name
ORDER BY parent.name, child.name;


-- ==============================================================================
-- QUERY 4: SPECIFICATION CHIPS INSPECTION (First 3 attributes used on Product Cards)
-- ==============================================================================
SELECT 
    p.sku,
    p.name AS product_name,
    STRING_AGG(pa.attr_key || ': ' || pa.attr_value, ' | ') AS spec_chips
FROM PRODUCTS p
LEFT JOIN PRODUCT_ATTRIBUTES pa ON p.id = pa.product_id
GROUP BY p.sku, p.name
ORDER BY p.sku;
