import Image from "next/image";
import Link from "next/link";
import { ProductGroupResponse, ProductResponse } from "@/types/catalog";
import { formatVndCurrency } from "@/utils/formatters";

export interface HeroPromoSectionProps {
  productGroups: ProductGroupResponse[];
  featuredProducts: ProductResponse[];
  selectedGroupId?: string;
}

export const HeroPromoSection = ({
  productGroups,
  featuredProducts,
  selectedGroupId,
}: HeroPromoSectionProps) => {
  const showcaseItems = featuredProducts
    .filter((product) => (product.imageUrls || []).length > 0)
    .slice(0, 2);

  return (
    <section className="space-y-8">
      {/* Editorial Asymmetric Hero */}
      <div
        className="rounded-3xl bg-[#FAF8F5] border border-[#EAE3DB]
          overflow-hidden grid grid-cols-1 lg:grid-cols-12"
      >
        <div className="lg:col-span-7 p-8 sm:p-12 lg:p-14 flex flex-col justify-center space-y-5">
          <span
            className="inline-flex w-fit items-center rounded-full
              bg-[#EFE8DF] px-3.5 py-1 text-[11px] font-semibold
              uppercase tracking-[0.14em] text-[#5C4033]"
          >
            Modern Living Collection
          </span>

          <h1
            className="font-serif text-3xl sm:text-4xl lg:text-5xl font-medium
              text-[#1F1916] leading-[1.15] tracking-tight"
          >
            Elevated essentials designed for everyday comfort.
          </h1>

          <p className="text-sm sm:text-base text-[#6E6259] max-w-xl leading-relaxed">
            Discover curated home appliances, living room electronics, and daily technology with
            transparent VND pricing and white-glove delivery.
          </p>

          <div className="flex flex-wrap items-center gap-3 pt-2">
            <Link
              href="#catalog-grid"
              className="inline-flex items-center justify-center rounded-full
                bg-[#3E2723] hover:bg-[#5C4033] px-7 py-3
                text-xs font-medium tracking-wide text-white transition-colors"
            >
              Shop Collection
            </Link>
            <Link
              href="/login"
              className="inline-flex items-center justify-center rounded-full
                border border-[#D8CFC4] bg-white hover:border-[#3E2723] px-7 py-3
                text-xs font-medium text-[#3E2723] transition-colors"
            >
              Member Sign In
            </Link>
          </div>
        </div>

        {/* Right Editorial Product Preview Panel */}
        <div
          className="lg:col-span-5 bg-[#F2ECE4] p-6 sm:p-8
            flex flex-col justify-center gap-4 border-t lg:border-t-0 lg:border-l border-[#EAE3DB]"
        >
          {showcaseItems.length > 0 ? (
            <div className="grid grid-cols-2 gap-4">
              {showcaseItems.map((item) => (
                <div
                  key={item.id}
                  className="rounded-2xl bg-white p-3.5 border border-[#EAE3DB]
                    shadow-sm space-y-2.5"
                >
                  <div
                    className="relative aspect-square w-full overflow-hidden
                      rounded-xl bg-[#FAF8F5]"
                  >
                    <Image
                      src={item.imageUrls[0]}
                      alt={item.name}
                      fill
                      sizes="220px"
                      className="object-contain p-2"
                    />
                  </div>
                  <div className="space-y-0.5">
                    <p className="text-[10px] uppercase tracking-wider text-[#8B5E3C] font-medium">
                      {item.brandName || "Featured"}
                    </p>
                    <p className="text-xs font-medium text-[#1F1916] line-clamp-1">{item.name}</p>
                    <p className="text-xs font-semibold text-[#3E2723]">
                      {formatVndCurrency(item.price)}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div
              className="rounded-2xl bg-white/80 border border-[#EAE3DB]
                p-8 text-center space-y-2"
            >
              <p className="font-serif text-lg text-[#3E2723]">Minimalist Retail Craft</p>
              <p className="text-xs text-[#6E6259]">
                Curated home, kitchen, and personal electronics.
              </p>
            </div>
          )}
        </div>
      </div>

      {/* Editorial Collection Cards */}
      {productGroups.length > 0 && (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3.5">
          {productGroups.map((group) => {
            const isSelected = selectedGroupId === group.id;
            return (
              <Link
                key={group.id}
                href={isSelected ? "/" : `/?group=${group.id}`}
                className={`rounded-2xl border px-5 py-4 text-left transition-all
                  flex items-center justify-between gap-3 ${
                    isSelected
                      ? "bg-[#3E2723] border-[#3E2723] text-white shadow-sm"
                      : "bg-white border-[#EAE3DB] text-[#1F1916] hover:border-[#8B5E3C]"
                  }`}
              >
                <div className="space-y-0.5 min-w-0">
                  <span
                    className={`block text-[10px] uppercase tracking-widest ${
                      isSelected ? "text-[#D9CBBF]" : "text-[#8B5E3C]"
                    }`}
                  >
                    Collection
                  </span>
                  <span className="block text-xs font-semibold truncate">{group.name}</span>
                </div>
                <span
                  className={`text-sm shrink-0 ${isSelected ? "text-white" : "text-[#8B5E3C]"}`}
                >
                  →
                </span>
              </Link>
            );
          })}
        </div>
      )}
    </section>
  );
};
