"use client";

import { useState } from "react";
import Image from "next/image";
import { ProductResponse } from "@/types/catalog";
import { formatPackageSpec, formatVndCurrency } from "@/utils/formatters";

export interface ProductQuickViewTriggerProps {
  product: ProductResponse;
}

export const ProductQuickViewTrigger = ({ product }: ProductQuickViewTriggerProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedImageIndex, setSelectedImageIndex] = useState(0);

  const images = [...(product.imageUrls || [])].sort((a, b) => a.localeCompare(b));
  const activeImage = images[selectedImageIndex] || images[0] || null;
  const attributes = [...(product.attributes || [])].sort((a, b) =>
    a.attrKey.localeCompare(b.attrKey),
  );
  const tags = [...(product.tags || [])].sort((a, b) => a.name.localeCompare(b.name));
  const packageSpec = formatPackageSpec(product.packageSize, product.unitOfMeasure);

  return (
    <>
      <button
        type="button"
        onClick={() => setIsOpen(true)}
        className="rounded-full border border-[#D8CFC4] bg-white
          px-3.5 py-1.5 text-[11px] font-medium text-[#3E2723]
          hover:bg-[#3E2723] hover:border-[#3E2723] hover:text-white
          transition-colors shrink-0"
      >
        Quick View
      </button>

      {isOpen && (
        <div
          className="fixed inset-0 z-50 bg-[#1F1916]/50 backdrop-blur-[2px]
            flex items-center justify-center p-4"
          role="dialog"
          aria-modal="true"
          onClick={() => setIsOpen(false)}
        >
          <div
            className="bg-white rounded-3xl shadow-2xl max-w-4xl w-full
              max-h-[90vh] overflow-y-auto border border-[#EAE3DB]"
            onClick={(e) => e.stopPropagation()}
          >
            <div
              className="sticky top-0 z-10 bg-white/95 backdrop-blur border-b border-[#EAE3DB]
                px-6 py-4 flex items-center justify-between gap-4"
            >
              <div>
                <p className="text-[11px] font-semibold uppercase tracking-widest text-[#8B5E3C]">
                  {product.brandName || "eSupermarket"} • {product.categoryName || "Catalog"}
                </p>
                <h2 className="font-serif text-lg font-medium text-[#1F1916]">{product.name}</h2>
              </div>
              <button
                type="button"
                aria-label="Close product details"
                onClick={() => setIsOpen(false)}
                className="w-9 h-9 rounded-full bg-[#FAF8F5] border border-[#EAE3DB]
                  hover:bg-[#3E2723] hover:text-white text-[#3E2723]
                  font-medium flex items-center justify-center transition-colors"
              >
                ×
              </button>
            </div>

            {/* Split Editorial Product Lightbox */}
            <div className="p-6 sm:p-8 grid grid-cols-1 lg:grid-cols-12 gap-8">
              {/* Left Column (col-span-7): Warm Gallery & Specs */}
              <div className="lg:col-span-7 space-y-5">
                <div className="rounded-2xl border border-[#EAE3DB] bg-[#FAF8F5] p-5">
                  <div className="relative aspect-square max-h-80 w-full overflow-hidden">
                    {activeImage ? (
                      <Image
                        src={activeImage}
                        alt={product.name}
                        fill
                        sizes="(max-width: 1024px) 100vw, 480px"
                        className="object-contain"
                      />
                    ) : (
                      <div
                        className="h-full w-full flex items-center justify-center
                          text-[#94877C]"
                      >
                        No image available
                      </div>
                    )}
                  </div>

                  {images.length > 1 && (
                    <div className="mt-4 flex gap-2.5 overflow-x-auto">
                      {images.map((url, idx) => (
                        <button
                          key={`${product.id}-thumb-${idx}`}
                          type="button"
                          onClick={() => setSelectedImageIndex(idx)}
                          className={`relative h-14 w-14 flex-shrink-0 rounded-xl
                            border p-1 overflow-hidden bg-white transition ${
                              idx === selectedImageIndex
                                ? "border-[#3E2723] ring-1 ring-[#3E2723]"
                                : "border-[#EAE3DB] hover:border-[#8B5E3C]"
                            }`}
                        >
                          <Image
                            src={url}
                            alt={`${product.name} thumbnail ${idx + 1}`}
                            fill
                            sizes="56px"
                            className="object-contain p-0.5"
                          />
                        </button>
                      ))}
                    </div>
                  )}
                </div>

                {/* Specifications Table */}
                <div className="rounded-2xl border border-[#EAE3DB] bg-white p-5">
                  <h3
                    className="text-[11px] font-semibold uppercase tracking-widest
                      text-[#6E6259] mb-3"
                  >
                    Specifications
                  </h3>
                  {attributes.length > 0 ? (
                    <div className="divide-y divide-[#F3EFEA] text-xs">
                      {attributes.map((attr) => (
                        <div key={attr.id} className="flex py-2.5 gap-4">
                          <span className="w-2/5 font-medium text-[#6E6259]">{attr.attrKey}</span>
                          <span className="w-3/5 text-[#1F1916]">{attr.attrValue}</span>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <p className="text-xs text-[#6E6259]">No specifications available.</p>
                  )}
                </div>
              </div>

              {/* Right Column (col-span-5): Pricing & Details */}
              <div className="lg:col-span-5 space-y-5">
                <div className="rounded-2xl bg-[#FAF8F5] border border-[#EAE3DB] p-5 space-y-3">
                  <div className="text-2xl font-semibold text-[#3E2723] tracking-tight">
                    {formatVndCurrency(product.price)}
                  </div>
                  <div
                    className="space-y-1.5 text-xs text-[#6E6259]
                      pt-1 border-t border-[#EAE3DB]"
                  >
                    <p>SKU: {product.sku}</p>
                    {product.barcode && <p>Barcode: {product.barcode}</p>}
                    {packageSpec && <p>Package Size: {packageSpec}</p>}
                    {product.supplierName && <p>Supplier: {product.supplierName}</p>}
                    {product.groupName && <p>Collection: {product.groupName}</p>}
                  </div>
                </div>

                {tags.length > 0 && (
                  <div className="space-y-2.5">
                    <p
                      className="text-[11px] font-semibold uppercase
                        tracking-widest text-[#6E6259]"
                    >
                      Highlights
                    </p>
                    <div className="flex flex-wrap gap-1.5">
                      {tags.map((tag) => (
                        <span
                          key={tag.id}
                          className="rounded-full bg-[#F5F1EC] border border-[#EAE3DB]
                            px-3 py-1 text-[11px] font-medium text-[#5C4033]"
                        >
                          {tag.name}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                <div
                  className="border-t border-[#EAE3DB] pt-4 text-xs text-[#6E6259]
                    space-y-2 leading-relaxed"
                >
                  <p>• Complimentary 2-hour city delivery</p>
                  <p>• 30-day doorstep return &amp; exchange guarantee</p>
                  <p>• Official brand warranty included</p>
                </div>

                <button
                  type="button"
                  onClick={() => setIsOpen(false)}
                  className="w-full rounded-full bg-[#3E2723] hover:bg-[#5C4033]
                    py-3 text-xs font-medium tracking-wide text-white transition-colors"
                >
                  Close Preview
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
};
