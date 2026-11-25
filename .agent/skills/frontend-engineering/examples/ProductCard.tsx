import React from "react";
import Link from "next/link";

export interface ProductSpec {
  name: string;
  value: string;
}

export interface ProductCardProps {
  id: string;
  sku: string;
  name: string;
  imageUrl: string;
  price: number;
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
  imageUrl,
  price,
  promoBadge,
  specs,
}) => {
  return (
    <div className="group flex flex-col justify-between">
      <div>
        {/* Warm Alabaster Image Stage */}
        <Link
          href={`/products/${id}`}
          className="relative block aspect-square w-full overflow-hidden rounded-2xl
            bg-[#FAF8F5] border border-[#EFEAE4]
            transition-colors duration-300 group-hover:border-[#8B5E3C]"
        >
          {promoBadge && (
            <span
              className="absolute top-3 left-3 z-10 rounded-full
                bg-white/95 px-2.5 py-1 text-[10px] font-semibold
                uppercase tracking-wider text-[#3E2723] border border-[#EAE3DB]"
            >
              {promoBadge}
            </span>
          )}
          <img
            src={imageUrl}
            alt={name}
            className="h-full w-full object-contain p-5
              transition-transform duration-500 group-hover:scale-105"
            loading="lazy"
          />
        </Link>

        {/* Product Title clamped to 2 lines */}
        <Link href={`/products/${id}`} className="mt-3.5 block">
          <h3
            className="line-clamp-2 min-h-[2.5rem] text-sm font-medium text-[#1F1916]
              leading-snug transition-colors group-hover:text-[#5C4033]"
          >
            {name}
          </h3>
        </Link>

        {/* Spec Attribute Chips Row */}
        <div className="mt-2 flex flex-wrap gap-1.5">
          {specs.slice(0, 2).map((spec) => (
            <span
              key={`${id}-${spec.name}`}
              className="rounded-full bg-[#F5F1EC] px-2.5 py-0.5
                text-[11px] text-[#5C4033]"
            >
              {spec.value}
            </span>
          ))}
        </div>
      </div>

      {/* Bottom Section: VND Price & Outline Pill CTA */}
      <div className="mt-3.5 flex items-center justify-between gap-3">
        <span className="text-base font-semibold text-[#3E2723] tracking-tight">
          {formatCurrency(price)}
        </span>
        <Link
          href={`/products/${id}`}
          className="rounded-full border border-[#D8CFC4] bg-white
            px-3.5 py-1.5 text-[11px] font-medium text-[#3E2723]
            hover:bg-[#3E2723] hover:border-[#3E2723] hover:text-white transition-colors"
        >
          Quick View
        </Link>
      </div>
    </div>
  );
};
