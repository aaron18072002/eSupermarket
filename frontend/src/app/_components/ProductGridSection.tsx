import Link from "next/link";
import { ProductCard } from "@/components/ProductCard";
import { ProductResponse } from "@/types/catalog";

export interface ProductGridSectionProps {
  title: string;
  products: ProductResponse[];
  errorMessage?: string | null;
}

export const ProductGridSection = ({ title, products, errorMessage }: ProductGridSectionProps) => {
  return (
    <section id="catalog-grid" className="space-y-6">
      <div className="flex flex-wrap items-end justify-between gap-2">
        <div className="space-y-1">
          <p className="text-[11px] font-semibold uppercase tracking-[0.14em] text-[#8B5E3C]">
            Catalog Selection
          </p>
          <h2 className="font-serif text-2xl sm:text-3xl font-medium text-[#1F1916] tracking-tight">
            {title}
          </h2>
        </div>
        <span className="text-xs font-medium text-[#6E6259]">{products.length} products</span>
      </div>

      {errorMessage && (
        <div
          className="rounded-xl border border-red-200 bg-[#FEF3F2]
            p-4 text-sm text-[#B42318] font-medium"
        >
          {errorMessage}
        </div>
      )}

      {!errorMessage && products.length === 0 && (
        <div
          className="rounded-2xl border border-dashed border-[#D8CFC4]
            bg-[#FAF8F5] p-12 text-center space-y-3"
        >
          <p className="font-serif text-lg text-[#1F1916]">No matching items found.</p>
          <p className="text-xs text-[#6E6259] max-w-md mx-auto">
            Try another keyword or reset active filters to browse the complete selection.
          </p>
          <Link
            href="/"
            className="inline-block mt-1 rounded-full bg-[#3E2723]
              px-6 py-2.5 text-xs font-medium text-white hover:bg-[#5C4033] transition-colors"
          >
            View All Products
          </Link>
        </div>
      )}

      {products.length > 0 && (
        <div
          className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4
            gap-x-6 gap-y-10"
        >
          {products.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      )}
    </section>
  );
};
