import React from "react";
import { notFound } from "next/navigation";
import { readResource } from "./apiClient";
import { ProductSpec } from "./ProductCard";

interface ProductDetailData {
  id: string;
  sku: string;
  name: string;
  brandName: string;
  imageUrls: string[];
  price: number;
  specs: ProductSpec[];
}

const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
};

export const ProductDetailPage = async ({ params }: { params: { id: string } }) => {
  const response = await readResource<ProductDetailData>(
    `/api/v1/products/${params.id}`,
    [`product-${params.id}`, "products"],
    60,
  );

  if (response.status !== 200 || !response.data) {
    notFound();
  }

  const product = response.data;

  return (
    <div className="mx-auto max-w-7xl px-4 sm:px-6 py-8">
      {/* Breadcrumb Navigation */}
      <nav className="mb-5 text-xs text-[#6E6259]">
        <span className="hover:text-[#3E2723]">Home</span> /{" "}
        <span className="hover:text-[#3E2723]">{product.brandName}</span> /{" "}
        <span className="text-[#1F1916]">{product.name}</span>
      </nav>

      {/* 2-Column Editorial Product Detail Page */}
      <div className="grid grid-cols-1 gap-8 lg:grid-cols-12">
        {/* Left Column (col-span-7): Warm Gallery & Specifications */}
        <div className="space-y-6 lg:col-span-7">
          <div className="rounded-2xl border border-[#EAE3DB] bg-[#FAF8F5] p-6">
            <div className="relative aspect-square w-full overflow-hidden">
              <img
                src={product.imageUrls[0]}
                alt={product.name}
                className="h-full w-full object-contain"
              />
            </div>
            <div className="mt-4 flex gap-2.5 overflow-x-auto">
              {product.imageUrls.map((imgUrl, index) => (
                <button
                  key={`${product.id}-img-${index}`}
                  className="h-16 w-16 flex-shrink-0 rounded-xl border
                    border-[#EAE3DB] bg-white p-1 hover:border-[#8B5E3C]"
                >
                  <img
                    src={imgUrl}
                    alt={`Thumbnail ${index + 1}`}
                    className="h-full w-full object-contain"
                  />
                </button>
              ))}
            </div>
          </div>

          <div className="rounded-2xl border border-[#EAE3DB] bg-white p-6">
            <h2 className="text-[11px] font-semibold uppercase tracking-widest text-[#6E6259]">
              Specifications
            </h2>
            <div className="mt-4 divide-y divide-[#F3EFEA] text-xs">
              {product.specs.map((spec, idx) => (
                <div key={`spec-${idx}`} className="flex py-2.5 gap-4">
                  <span className="w-1/3 font-medium text-[#6E6259]">{spec.name}</span>
                  <span className="w-2/3 text-[#1F1916]">{spec.value}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Right Column (col-span-5): Pricing & Details */}
        <div className="lg:col-span-5">
          <div
            className="sticky top-24 space-y-5 rounded-2xl
              border border-[#EAE3DB] bg-white p-6"
          >
            <div>
              <span className="text-[11px] font-semibold uppercase tracking-widest text-[#8B5E3C]">
                {product.brandName}
              </span>
              <h1 className="mt-1 font-serif text-2xl font-medium text-[#1F1916]">
                {product.name}
              </h1>
              <p className="mt-1 text-xs text-[#6E6259]">SKU: {product.sku}</p>
            </div>

            <div className="rounded-2xl bg-[#FAF8F5] border border-[#EAE3DB] p-4">
              <div className="text-2xl font-semibold text-[#3E2723]">
                {formatCurrency(product.price)}
              </div>
              <p className="mt-1 text-[11px] text-[#6E6259]">Complimentary same-day delivery</p>
            </div>

            <button
              className="w-full rounded-full bg-[#3E2723] py-3 text-xs font-medium
                tracking-wide text-white transition-colors hover:bg-[#5C4033]"
            >
              Add to Bag
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
