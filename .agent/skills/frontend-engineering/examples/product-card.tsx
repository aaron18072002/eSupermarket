import React from "react";
import Link from "next/link";

export interface ProductSpec {
  name: string;
  value: string;
}

export interface ProductCardProps {
  id: number;
  sku: string;
  name: string;
  slug: string;
  imageUrl: string;
  regularPrice: number;
  salePrice: number;
  discountPercent?: number;
  promoBadge?: string;
  specs: ProductSpec[];
}

const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
};

export const ProductCard: React.FC<ProductCardProps> = ({
  id,
  name,
  slug,
  imageUrl,
  regularPrice,
  salePrice,
  discountPercent,
  promoBadge,
  specs,
}) => {
  return (
    <div
      className="group relative flex flex-col justify-between rounded-lg border border-gray-200 "
      + "bg-white p-3 transition-all duration-200 hover:-translate-y-0.5 hover:border-[#2A83E9] hover:shadow-lg"
    >
      <div>
        {/* Top Promotional Accent Badge */}
        <div className="mb-2 flex h-6 items-center justify-between">
          {promoBadge ? (
            <span className="rounded bg-[#FFD400] px-2 py-0.5 text-[11px] font-bold text-gray-900">
              {promoBadge}
            </span>
          ) : (
            <span />
          )}
          {discountPercent && discountPercent > 0 ? (
            <span className="rounded bg-red-50 px-1.5 py-0.5 text-[11px] font-bold text-[#D0021B]">
              -{discountPercent}%
            </span>
          ) : null}
        </div>

        {/* Product Image Thumbnail */}
        <Link href={`/product/${slug}`} className="block overflow-hidden">
          <div className="relative aspect-square w-full overflow-hidden bg-white">
            <img
              src={imageUrl}
              alt={name}
              className="h-full w-full object-contain transition-transform duration-300 group-hover:scale-105"
              loading="lazy"
            />
          </div>
        </Link>

        {/* Product Title clamped to 2 lines */}
        <Link href={`/product/${slug}`} className="mt-2 block">
          <h3 className="line-clamp-2 text-xs font-medium text-gray-800 transition-colors group-hover:text-[#2A83E9]">
            {name}
          </h3>
        </Link>

        {/* Spec Attribute Chips Row */}
        <div className="mt-2 flex flex-wrap gap-1">
          {specs.slice(0, 3).map((spec) => (
            <span
              key={`${id}-${spec.name}`}
              className="rounded bg-gray-100 px-1.5 py-0.5 text-[10px] font-medium text-gray-600"
            >
              {spec.value}
            </span>
          ))}
        </div>
      </div>

      {/* Bottom Section: Price & CTA Hierarchy */}
      <div className="mt-3 pt-2">
        <div className="flex items-baseline gap-1.5">
          <span className="text-sm font-bold text-[#D0021B]">{formatCurrency(salePrice)}</span>
          {regularPrice > salePrice ? (
            <span className="text-[11px] text-[#9E9E9E] line-through">
              {formatCurrency(regularPrice)}
            </span>
          ) : null}
        </div>

        {/* Conversion Orange CTA Button */}
        <div className="mt-2">
          <Link
            href={`/product/${slug}`}
            className="flex w-full items-center justify-center rounded bg-[#FF6600] py-1.5 text-xs "
            + "font-semibold text-white transition-colors hover:bg-[#E65C00]"
          >
            MUA NGAY
          </Link>
        </div>
      </div>
    </div>
  );
};
