export interface ProductAttributeResponse {
  id: string;
  attrKey: string;
  attrValue: string;
}

export interface TagResponse {
  id: string;
  name: string;
  createdAt: string;
  updatedAt: string;
}

export interface CategoryResponse {
  id: string;
  name: string;
  parentId: string | null;
  parentName: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface BrandResponse {
  id: string;
  name: string;
  country: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ProductGroupResponse {
  id: string;
  name: string;
  createdAt: string;
  updatedAt: string;
}

export interface ProductResponse {
  id: string;
  sku: string;
  barcode: string | null;
  name: string;
  price: number;
  packageSize: number | null;
  unitOfMeasure: string | null;
  categoryId: string | null;
  categoryName: string | null;
  brandId: string | null;
  brandName: string | null;
  supplierId: string | null;
  supplierName: string | null;
  groupId: string | null;
  groupName: string | null;
  tags: TagResponse[];
  attributes: ProductAttributeResponse[];
  imageUrls: string[];
  createdAt: string;
  updatedAt: string;
}
