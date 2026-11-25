import Image from "next/image";
import { ProductQuickViewTrigger } from "@/app/_components/ProductQuickViewTrigger";
import { ProductResponse } from "@/types/catalog";
import { formatPackageSpec, formatVndCurrency } from "@/utils/formatters";

export interface ProductCardProps {
  product: ProductResponse;
}

export const ProductCard = ({ product }: ProductCardProps) => {
  const sortedImages = [...(product.imageUrls || [])].sort((a, b) => a.localeCompare(b));
  const primaryImage = sortedImages[0] || null;

  const sortedTags = [...(product.tags || [])].sort((a, b) => a.name.localeCompare(b.name));
  const primaryTag = sortedTags[0]?.name || null;

  const sortedAttributes = [...(product.attributes || [])].sort((a, b) =>
    a.attrKey.localeCompare(b.attrKey),
  );
  const packageSpec = formatPackageSpec(product.packageSize, product.unitOfMeasure);

  return (
    <article className="group flex flex-col justify-between">
      <div>
        {/* Minimal Warm Alabaster Image Stage */}
        <div
          className="relative aspect-square w-full overflow-hidden rounded-2xl
            bg-[#FAF8F5] border border-[#EFEAE4]
            transition-colors duration-300 group-hover:border-[#8B5E3C]"
        >
          {primaryTag && (
            <span
              className="absolute top-3 left-3 z-10 rounded-full
                bg-white/95 backdrop-blur px-2.5 py-1
                text-[10px] font-semibold uppercase tracking-wider text-[#3E2723]
                border border-[#EAE3DB]"
            >
              {primaryTag}
            </span>
          )}
          {packageSpec && (
            <span
              className="absolute top-3 right-3 z-10 rounded-full
                bg-[#F2ECE4]/95 px-2.5 py-1
                text-[10px] font-medium text-[#5C4033]"
            >
              {packageSpec}
            </span>
          )}

          {primaryImage ? (
            <Image
              src={primaryImage}
              alt={product.name}
              fill
              sizes="(max-width: 640px) 100vw, (max-width: 1024px) 50vw, 25vw"
              className="object-contain p-5 transition-transform duration-500 group-hover:scale-105"
            />
          ) : (
            <div
              className="h-full w-full flex items-center justify-center
                text-xs font-medium text-[#94877C]"
            >
              {product.sku}
            </div>
          )}
        </div>

        {/* Minimalist Gallery Metadata Below Image */}
        <div className="pt-3.5 space-y-1.5">
          <div
            className="flex items-center justify-between gap-2
              text-[10px] uppercase tracking-widest text-[#8B5E3C] font-semibold"
          >
            <span className="truncate">{product.brandName || "Collection"}</span>
            <span className="text-[#94877C] font-normal truncate">
              {product.categoryName || ""}
            </span>
          </div>

          <h3
            className="line-clamp-2 min-h-[2.5rem] text-sm font-medium text-[#1F1916]
              leading-snug transition-colors group-hover:text-[#5C4033]"
          >
            {product.name}
          </h3>

          {sortedAttributes.length > 0 && (
            <div className="flex flex-wrap gap-1.5 pt-0.5">
              {sortedAttributes.slice(0, 2).map((attribute) => (
                <span
                  key={attribute.id}
                  className="rounded-full bg-[#F5F1EC] px-2.5 py-0.5
                    text-[11px] text-[#5C4033] truncate max-w-full"
                >
                  {attribute.attrValue}
                </span>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Price (VND) & Subtle Outline Quick View Action */}
      <div className="pt-3.5 flex items-center justify-between gap-3">
        <span className="text-base font-semibold text-[#3E2723] tracking-tight">
          {formatVndCurrency(product.price)}
        </span>
        <ProductQuickViewTrigger product={product} />
      </div>
    </article>
  );
};
