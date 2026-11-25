import { CategoryFilterBar, CatalogFilterParams } from "@/app/_components/CategoryFilterBar";
import { HeroPromoSection } from "@/app/_components/HeroPromoSection";
import { ProductGridSection } from "@/app/_components/ProductGridSection";
import { StoreFooter } from "@/components/StoreFooter";
import { StoreHeader } from "@/components/StoreHeader";
import {
  readBrands,
  readCategories,
  readProductGroups,
  readProducts,
  readTags,
  searchProducts,
} from "@/services/catalogService";
import { CategoryResponse, ProductResponse } from "@/types/catalog";

interface HomePageProps {
  searchParams: CatalogFilterParams;
}

const resolveAllowedCategoryIds = (
  categories: CategoryResponse[],
  selectedCategoryId?: string,
): Set<string> | null => {
  if (!selectedCategoryId) {
    return null;
  }

  const allowedIds = new Set<string>([selectedCategoryId]);
  categories.forEach((category) => {
    if (category.parentId === selectedCategoryId) {
      allowedIds.add(category.id);
    }
  });
  return allowedIds;
};

const filterCatalogProducts = (
  products: ProductResponse[],
  categories: CategoryResponse[],
  filters: CatalogFilterParams,
): ProductResponse[] => {
  const allowedCategoryIds = resolveAllowedCategoryIds(categories, filters.category);

  return products.filter((product) => {
    if (
      allowedCategoryIds &&
      (!product.categoryId || !allowedCategoryIds.has(product.categoryId))
    ) {
      return false;
    }
    if (filters.brand && product.brandId !== filters.brand) {
      return false;
    }
    if (filters.group && product.groupId !== filters.group) {
      return false;
    }
    if (filters.tag && !(product.tags || []).some((tag) => tag.id === filters.tag)) {
      return false;
    }
    return true;
  });
};

export default async function HomePage({ searchParams }: HomePageProps) {
  const searchQuery = searchParams.q?.trim() || "";
  const activeFilters: CatalogFilterParams = {
    q: searchQuery || undefined,
    category: searchParams.category,
    brand: searchParams.brand,
    tag: searchParams.tag,
    group: searchParams.group,
  };

  const [
    productsResponse,
    categoriesResponse,
    brandsResponse,
    tagsResponse,
    productGroupsResponse,
  ] = await Promise.all([
    searchQuery ? searchProducts(searchQuery) : readProducts(),
    readCategories(),
    readBrands(),
    readTags(),
    readProductGroups(),
  ]);

  const categories = categoriesResponse.data ?? [];
  const rootCategories = categories.filter((category) => category.parentId === null);
  const brands = brandsResponse.data ?? [];
  const tags = tagsResponse.data ?? [];
  const productGroups = productGroupsResponse.data ?? [];

  const allProducts = productsResponse.data ?? [];
  const filteredProducts = filterCatalogProducts(allProducts, categories, activeFilters);

  const selectedCategory = categories.find((cat) => cat.id === activeFilters.category);
  const activeRootCategoryId = selectedCategory?.parentId ?? selectedCategory?.id;
  const selectedGroup = productGroups.find((group) => group.id === activeFilters.group);

  let sectionTitle = "Featured Selection";
  if (searchQuery) {
    sectionTitle = `Search Results: "${searchQuery}"`;
  } else if (selectedCategory) {
    sectionTitle = selectedCategory.name;
  } else if (selectedGroup) {
    sectionTitle = selectedGroup.name;
  }

  const errorMessage =
    productsResponse.status !== 200 || !productsResponse.data ? productsResponse.message : null;

  return (
    <div className="min-h-screen flex flex-col bg-white">
      <StoreHeader
        rootCategories={rootCategories}
        selectedCategoryId={activeRootCategoryId}
        searchQuery={searchQuery}
      />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 py-8 space-y-8">
        <HeroPromoSection
          productGroups={productGroups}
          featuredProducts={allProducts}
          selectedGroupId={activeFilters.group}
        />

        <CategoryFilterBar
          categories={categories}
          brands={brands}
          tags={tags}
          activeFilters={activeFilters}
          activeRootCategoryId={activeRootCategoryId}
        />

        <ProductGridSection
          title={sectionTitle}
          products={filteredProducts}
          errorMessage={errorMessage}
        />
      </main>

      <StoreFooter />
    </div>
  );
}
