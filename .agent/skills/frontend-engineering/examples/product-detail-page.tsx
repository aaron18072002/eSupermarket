import React from "react";
import { notFound } from "next/navigation";
import { readResource } from "./api-client";
import { ProductSpec } from "./product-card";

interface ProductDetailData {
  id: number;
  sku: string;
  name: string;
  slug: string;
  brand: string;
  images: string[];
  regularPrice: number;
  salePrice: number;
  discountPercent: number;
  inStock: boolean;
  specs: ProductSpec[];
  highlights: string[];
}

const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(amount);
};

export const ProductDetailPage = async ({ params }: { params: { slug: string } }) => {
  let product: ProductDetailData;

  try {
    product = await readResource<ProductDetailData>(
      `/api/v1/products/${params.slug}`,
      [`product-${params.slug}`, "products"],
      60
    );
  } catch {
    notFound();
  }

  return (
    <div className="mx-auto max-w-7xl px-4 py-6">
      {/* Breadcrumb Navigation */}
      <nav className="mb-4 text-xs text-gray-500">
        <span className="hover:text-[#2A83E9]">Trang chủ</span> /{" "}
        <span className="hover:text-[#2A83E9]">{product.brand}</span> /{" "}
        <span className="text-gray-800">{product.name}</span>
      </nav>

      {/* 2-Column Product Detail Page Grid */}
      <div className="grid grid-cols-1 gap-8 lg:grid-cols-12">
        {/* Left Column (col-span-7): Media Gallery & Technical Specifications */}
        <div className="space-y-6 lg:col-span-7">
          {/* Main Showcase Image */}
          <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
            <div className="relative aspect-square w-full overflow-hidden">
              <img
                src={product.images[0]}
                alt={product.name}
                className="h-full w-full object-contain"
              />
            </div>
            {/* Gallery Thumbnails */}
            <div className="mt-4 flex gap-2 overflow-x-auto">
              {product.images.map((imgUrl, index) => (
                <button
                  key={`${product.id}-img-${index}`}
                  className="h-16 w-16 flex-shrink-0 rounded border border-gray-300 p-1 hover:border-[#2A83E9]"
                >
                  <img
                    src={imgUrl}
                    alt={`Thumbnail ${index + 1}`}
                    className="h-full w-full object-contain"
                  />
                </button>
              ))}
            </div>
          </div>

          {/* Highlights Box */}
          <div className="rounded-lg border border-gray-200 bg-white p-5 shadow-sm">
            <h2 className="text-sm font-bold uppercase text-gray-900">
              Đặc điểm nổi bật
            </h2>
            <ul className="mt-3 space-y-2 text-xs text-gray-700">
              {product.highlights.map((point, idx) => (
                <li key={`highlight-${idx}`} className="flex items-start gap-2">
                  <span className="text-[#2A83E9]">✔</span>
                  <span>{point}</span>
                </li>
              ))}
            </ul>
          </div>

          {/* Technical Specifications Table */}
          <div className="rounded-lg border border-gray-200 bg-white p-5 shadow-sm">
            <h2 className="text-sm font-bold uppercase text-gray-900">
              Thông số kỹ thuật
            </h2>
            <div className="mt-4 divide-y divide-gray-100 text-xs">
              {product.specs.map((spec, idx) => (
                <div
                  key={`spec-${idx}`}
                  className={`flex py-2.5 px-3 ${idx % 2 === 0 ? "bg-gray-50" : "bg-white"}`}
                >
                  <span className="w-1/3 font-medium text-gray-600">{spec.name}</span>
                  <span className="w-2/3 text-gray-900">{spec.value}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Right Column (col-span-5): Sticky Purchasing Hub */}
        <div className="lg:col-span-5">
          <div className="sticky top-20 space-y-4 rounded-lg border border-gray-200 bg-white p-5 shadow-sm">
            {/* Title & Brand */}
            <div>
              <span className="text-xs font-semibold text-[#2A83E9]">
                Thương hiệu: {product.brand}
              </span>
              <h1 className="mt-1 text-lg font-bold text-gray-900">{product.name}</h1>
              <p className="text-xs text-gray-400">Mã SKU: {product.sku}</p>
            </div>

            {/* Price Block: #D0021B conversion red & original strikethrough */}
            <div className="rounded-md bg-gray-50 p-3">
              <div className="flex items-baseline gap-2">
                <span className="text-2xl font-extrabold text-[#D0021B]">
                  {formatCurrency(product.salePrice)}
                </span>
                {product.regularPrice > product.salePrice ? (
                  <span className="text-xs text-[#9E9E9E] line-through">
                    {formatCurrency(product.regularPrice)}
                  </span>
                ) : null}
                {product.discountPercent > 0 ? (
                  <span className="rounded bg-red-100 px-1.5 py-0.5 text-xs font-bold text-[#D0021B]">
                    -{product.discountPercent}%
                  </span>
                ) : null}
              </div>
              <p className="mt-1 text-[11px] text-green-600">✔ Miễn phí vận chuyển toàn quốc</p>
            </div>

            {/* Promotional Yellow Box */}
            <div className="rounded-md border border-[#FFD400] bg-amber-50/50 p-3">
              <div className="flex items-center gap-1.5 text-xs font-bold text-gray-900">
                <span className="rounded bg-[#FFD400] px-1.5 py-0.5 text-[10px] text-gray-900">
                  KHUYẾN MÃI
                </span>
                <span>Ưu đãi áp dụng khi mua online</span>
              </div>
              <ul className="mt-2 space-y-1 text-xs text-gray-700">
                <li>• Tặng phiếu mua hàng trị giá 200.000₫.</li>
                <li>• Giảm thêm 5% khi thanh toán chuyển khoản hoặc QR code.</li>
                <li>• Bảo hành chính hãng 24 tháng tận nhà.</li>
              </ul>
            </div>

            {/* CTAs: Conversion Orange #FF6600 Primary CTA */}
            <div className="space-y-2 pt-2">
              <button
                className="w-full rounded-md bg-[#FF6600] py-3 text-sm font-bold uppercase "
                + "text-white shadow transition-colors hover:bg-[#E65C00]"
              >
                MUA NGAY
                <span className="block text-[11px] font-normal normal-case opacity-90">
                  Giao tận nơi hoặc nhận tại cửa hàng
                </span>
              </button>

              <div className="grid grid-cols-2 gap-2">
                <button
                  className="rounded-md border border-[#2A83E9] py-2 text-xs font-bold "
                  + "text-[#2A83E9] transition-colors hover:bg-blue-50"
                >
                  TRẢ GÓP 0%
                  <span className="block text-[10px] font-normal text-gray-500">
                    Duyệt hồ sơ nhanh qua ĐT
                  </span>
                </button>
                <button
                  className="rounded-md border border-gray-300 py-2 text-xs font-bold "
                  + "text-gray-700 transition-colors hover:bg-gray-50"
                >
                  THÊM VÀO GIỎ
                  <span className="block text-[10px] font-normal text-gray-500">
                    Chọn thêm sản phẩm khác
                  </span>
                </button>
              </div>
            </div>

            {/* Delivery & Warranty Policies */}
            <div className="border-t border-gray-100 pt-3 text-xs text-gray-600">
              <p>✔ Giao hàng nhanh trong 2 giờ</p>
              <p className="mt-1">✔ Hư gì đổi nấy trong 30 ngày tận nhà</p>
              <p className="mt-1">✔ Đội ngũ kỹ thuật viên lắp đặt chuyên nghiệp</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
