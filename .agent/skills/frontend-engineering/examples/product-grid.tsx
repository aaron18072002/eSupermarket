import React from "react";
import { readResource } from "./api-client";
import { ProductCard, ProductCardProps } from "./product-card";

interface ProductGridProps {
  categorySlug?: string;
  limit?: number;
}

/**
 * React Server Component (RSC) Product Grid
 * Fetches product catalog directly from API Gateway (:8080) with cache tags & ISR
 */
export const ProductGrid: React.FC<ProductGridProps> = async ({
  categorySlug,
  limit = 20,
}) => {
  const endpoint = categorySlug
    ? `/api/v1/products?category=${categorySlug}&limit=${limit}`
    : `/api/v1/products?limit=${limit}`;

  const cacheTags = categorySlug
    ? ["products", `category-${categorySlug}`]
    : ["products"];

  const products = await readResource<ProductCardProps[]>(
    endpoint,
    cacheTags,
    60,
  );

  if (!products || products.length === 0) {
    return (
      <div className="rounded-lg border border-dashed border-gray-300 p-8 text-center text-sm text-gray-500">
        Không tìm thấy sản phẩm phù hợp trong danh mục này.
      </div>
    );
  }

  return (
    <div className="grid grid-cols-2 gap-2.5 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
      {products.map((product) => (
        <ProductCard
          key={product.id}
          id={product.id}
          sku={product.sku}
          name={product.name}
          slug={product.slug}
          imageUrl={product.imageUrl}
          regularPrice={product.regularPrice}
          salePrice={product.salePrice}
          discountPercent={product.discountPercent}
          promoBadge={product.promoBadge}
          specs={product.specs}
        />
      ))}
    </div>
  );
};
