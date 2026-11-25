import Link from "next/link";
import { BrandResponse, CategoryResponse, TagResponse } from "@/types/catalog";

export interface CatalogFilterParams {
  q?: string;
  category?: string;
  brand?: string;
  tag?: string;
  group?: string;
}

export interface CategoryFilterBarProps {
  categories: CategoryResponse[];
  brands: BrandResponse[];
  tags: TagResponse[];
  activeFilters: CatalogFilterParams;
  activeRootCategoryId?: string;
}

const buildFilterHref = (
  current: CatalogFilterParams,
  updates: Partial<CatalogFilterParams>,
): string => {
  const merged: CatalogFilterParams = { ...current, ...updates };
  const queryParams = new URLSearchParams();

  if (merged.q && merged.q.trim().length > 0) {
    queryParams.set("q", merged.q.trim());
  }
  if (merged.category) {
    queryParams.set("category", merged.category);
  }
  if (merged.brand) {
    queryParams.set("brand", merged.brand);
  }
  if (merged.tag) {
    queryParams.set("tag", merged.tag);
  }
  if (merged.group) {
    queryParams.set("group", merged.group);
  }

  const queryString = queryParams.toString();
  return queryString ? `/?${queryString}` : "/";
};

export const CategoryFilterBar = ({
  categories,
  brands,
  tags,
  activeFilters,
  activeRootCategoryId,
}: CategoryFilterBarProps) => {
  const subcategories = activeRootCategoryId
    ? categories.filter((category) => category.parentId === activeRootCategoryId)
    : [];

  const hasAnyFilter = Boolean(
    activeFilters.q ||
      activeFilters.category ||
      activeFilters.brand ||
      activeFilters.tag ||
      activeFilters.group,
  );

  return (
    <section className="border-y border-[#EAE3DB] py-5 space-y-3.5">
      {/* Subcategories Row when a Department is Selected */}
      {subcategories.length > 0 && (
        <div className="flex flex-wrap items-center gap-2 pb-2 border-b border-[#F3EFEA]">
          <span className="text-[11px] font-semibold uppercase tracking-wider text-[#6E6259] mr-1">
            Category
          </span>
          <Link
            href={buildFilterHref(activeFilters, { category: activeRootCategoryId })}
            className={`px-3.5 py-1 rounded-full border text-xs
              transition-colors whitespace-nowrap ${
                activeFilters.category === activeRootCategoryId
                  ? "bg-[#3E2723] border-[#3E2723] text-white font-medium"
                  : "bg-[#FAF8F5] border-[#EAE3DB] text-[#5C4033] hover:border-[#8B5E3C]"
              }`}
          >
            All
          </Link>
          {subcategories.map((sub) => (
            <Link
              key={sub.id}
              href={buildFilterHref(activeFilters, { category: sub.id })}
              className={`px-3.5 py-1 rounded-full border text-xs
                transition-colors whitespace-nowrap ${
                  activeFilters.category === sub.id
                    ? "bg-[#3E2723] border-[#3E2723] text-white font-medium"
                    : "bg-[#FAF8F5] border-[#EAE3DB] text-[#5C4033] hover:border-[#8B5E3C]"
                }`}
            >
              {sub.name}
            </Link>
          ))}
        </div>
      )}

      {/* Brand Filter Strip */}
      {brands.length > 0 && (
        <div className="flex items-center gap-2 overflow-x-auto pb-1">
          <span
            className="text-[11px] font-semibold uppercase tracking-wider
              text-[#6E6259] shrink-0 mr-1"
          >
            Brands
          </span>
          {brands.map((brand) => {
            const isSelected = activeFilters.brand === brand.id;
            return (
              <Link
                key={brand.id}
                href={buildFilterHref(activeFilters, {
                  brand: isSelected ? undefined : brand.id,
                })}
                className={`px-3.5 py-1 rounded-full border text-xs
                  transition-colors whitespace-nowrap shrink-0 ${
                    isSelected
                      ? "bg-[#3E2723] border-[#3E2723] text-white font-medium"
                      : "bg-white border-[#EAE3DB] text-[#5C4033] hover:border-[#8B5E3C]"
                  }`}
              >
                {brand.name}
              </Link>
            );
          })}
        </div>
      )}

      {/* Editorial Highlights & Clear Filters */}
      <div className="flex flex-wrap items-center justify-between gap-2">
        <div className="flex flex-wrap items-center gap-2">
          <span className="text-[11px] font-semibold uppercase tracking-wider text-[#6E6259] mr-1">
            Tags
          </span>
          {tags.map((tag) => {
            const isSelected = activeFilters.tag === tag.id;
            return (
              <Link
                key={tag.id}
                href={buildFilterHref(activeFilters, {
                  tag: isSelected ? undefined : tag.id,
                })}
                className={`px-3 py-1 rounded-full border text-xs
                  transition-colors whitespace-nowrap ${
                    isSelected
                      ? "bg-[#8B5E3C] border-[#8B5E3C] text-white font-medium"
                      : "bg-[#FAF8F5] border-[#EAE3DB] text-[#6E6259] hover:border-[#8B5E3C]"
                  }`}
              >
                {tag.name}
              </Link>
            );
          })}
        </div>

        {hasAnyFilter && (
          <Link
            href="/"
            className="text-xs font-medium text-[#8B5E3C] underline
              underline-offset-4 hover:text-[#3E2723] whitespace-nowrap"
          >
            Reset Filters
          </Link>
        )}
      </div>
    </section>
  );
};
