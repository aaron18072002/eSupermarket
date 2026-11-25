import React from "react";
import { readResource } from "./apiClient";
import { ProductCard, ProductCardProps } from "./ProductCard";

interface ProductGridProps {
  categoryId?: string;
}

/**
 * React Server Component (RSC) Product Grid
 * Fetches product catalog directly from API Gateway (:8080) with cache tags & ISR
 */
export const ProductGrid: React.FC<ProductGridProps> = async ({ categoryId }) => {
  const endpoint = categoryId
    ? `/api/v1/products/category/${categoryId}`
    : "/api/v1/products";

  const cacheTags = categoryId ? ["products", `category-${categoryId}`] : ["products"];

  const response = await readResource<ProductCardProps[]>(endpoint, cacheTags, 60);
  const products = response.data ?? [];

  if (products.length === 0) {
    return (
      <div
        className="rounded-2xl border border-dashed border-[#D8CFC4]
          bg-[#FAF8F5] p-10 text-center text-sm text-[#6E6259]"
      >
        No products found in this category.
      </div>
    );
  }

  return (
    <div
      className="grid grid-cols-1 gap-x-6 gap-y-10
        sm:grid-cols-2 lg:grid-cols-4"
    >
      {products.map((product) => (
        <ProductCard
          key={product.id}
          id={product.id}
          sku={product.sku}
          name={product.name}
          imageUrl={product.imageUrl}
          price={product.price}
          promoBadge={product.promoBadge}
          specs={product.specs}
        />
      ))}
    </div>
  );
};
