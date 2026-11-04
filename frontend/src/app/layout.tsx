import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "eSupermarket | Hệ thống siêu thị bán lẻ điện máy & tiêu dùng",
  description: "Hệ thống siêu thị bán lẻ điện máy & tiêu dùng uy tín, giá tốt và giao hàng nhanh.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="vi">
      <body className="min-h-screen bg-[#F3F4F6] text-gray-900 antialiased">{children}</body>
    </html>
  );
}
