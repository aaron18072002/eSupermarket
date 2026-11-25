import Link from "next/link";
import { HeaderAccountActions } from "@/components/HeaderAccountActions";
import { CategoryResponse } from "@/types/catalog";

export interface StoreHeaderProps {
  rootCategories: CategoryResponse[];
  selectedCategoryId?: string;
  searchQuery?: string;
}

export const StoreHeader = ({
  rootCategories,
  selectedCategoryId,
  searchQuery = "",
}: StoreHeaderProps) => {
  return (
    <header className="sticky top-0 z-40 bg-white/95 backdrop-blur border-b border-[#EAE3DB]">
      {/* Minimal Espresso Announcement Strip */}
      <div className="bg-[#3E2723] text-[#F5EFEA] text-[11px] tracking-[0.12em] uppercase">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 py-1.5 flex items-center justify-between">
          <span>Curated Quality Essentials</span>
          <span className="hidden sm:inline text-[#D9CBBF]">
            Complimentary Same-Day Delivery • Official Warranty
          </span>
        </div>
      </div>

      {/* Airy White Main Navigation Bar */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 py-4 flex flex-wrap items-center gap-4">
        <Link href="/" className="flex items-center gap-2.5 shrink-0 mr-2">
          <span
            className="w-8 h-8 rounded-full bg-[#3E2723] text-white
              font-serif text-sm font-semibold flex items-center justify-center"
          >
            e
          </span>
          <span className="font-serif text-xl font-semibold tracking-tight text-[#1F1916]">
            eSupermarket
          </span>
        </Link>

        {/* Minimalist Search Pill */}
        <form action="/" method="GET" className="flex-1 min-w-[220px] max-w-md ml-auto relative">
          <input
            type="text"
            name="q"
            defaultValue={searchQuery}
            placeholder="Search products, brands, SKU..."
            className="w-full h-10 pl-4 pr-20 rounded-full bg-[#FAF8F5]
              border border-[#E5DDD3] text-xs text-[#1F1916] placeholder-[#94877C]
              focus:outline-none focus:bg-white focus:border-[#5C4033] transition-colors"
          />
          <button
            type="submit"
            className="absolute right-1.5 top-1.5 h-7 px-3.5 rounded-full
              bg-[#3E2723] hover:bg-[#5C4033] text-white
              text-[11px] font-medium transition-colors"
          >
            Search
          </button>
        </form>

        <div className="shrink-0">
          <HeaderAccountActions />
        </div>
      </div>

      {/* Editorial Department Underline Navigation */}
      <nav className="border-t border-[#F3EFEA] bg-white">
        <div
          className="max-w-7xl mx-auto px-4 sm:px-6 flex items-center
            gap-6 overflow-x-auto"
        >
          <Link
            href="/"
            className={`whitespace-nowrap py-3 text-xs font-medium border-b-2
              transition-colors ${
                !selectedCategoryId
                  ? "border-[#3E2723] text-[#1F1916]"
                  : "border-transparent text-[#6E6259] hover:text-[#1F1916]"
              }`}
          >
            All Products
          </Link>
          {rootCategories.map((category) => (
            <Link
              key={category.id}
              href={`/?category=${category.id}`}
              className={`whitespace-nowrap py-3 text-xs font-medium border-b-2
                transition-colors ${
                  selectedCategoryId === category.id
                    ? "border-[#3E2723] text-[#1F1916]"
                    : "border-transparent text-[#6E6259] hover:text-[#1F1916]"
                }`}
            >
              {category.name}
            </Link>
          ))}
        </div>
      </nav>
    </header>
  );
};
