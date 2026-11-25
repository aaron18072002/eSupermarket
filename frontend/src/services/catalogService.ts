import { readGatewayResource } from "@/services/apiClient";
import { ApiResponse } from "@/types/api";
import {
  BrandResponse,
  CategoryResponse,
  ProductGroupResponse,
  ProductResponse,
  TagResponse,
} from "@/types/catalog";

export const readProducts = async (): Promise<ApiResponse<ProductResponse[]>> => {
  return readGatewayResource<ProductResponse[]>("/api/v1/products", ["products"], 60);
};

export const searchProducts = async (query: string): Promise<ApiResponse<ProductResponse[]>> => {
  const encodedQuery = encodeURIComponent(query.trim());
  return readGatewayResource<ProductResponse[]>(
    `/api/v1/products/search?query=${encodedQuery}`,
    ["products"],
    0,
  );
};

export const readCategories = async (): Promise<ApiResponse<CategoryResponse[]>> => {
  return readGatewayResource<CategoryResponse[]>("/api/v1/categories", ["categories"], 60);
};

export const readBrands = async (): Promise<ApiResponse<BrandResponse[]>> => {
  return readGatewayResource<BrandResponse[]>("/api/v1/brands", ["brands"], 60);
};

export const readTags = async (): Promise<ApiResponse<TagResponse[]>> => {
  return readGatewayResource<TagResponse[]>("/api/v1/tags", ["tags"], 60);
};

export const readProductGroups = async (): Promise<ApiResponse<ProductGroupResponse[]>> => {
  return readGatewayResource<ProductGroupResponse[]>(
    "/api/v1/product-groups",
    ["product-groups"],
    60,
  );
};
