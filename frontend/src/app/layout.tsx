import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "eSupermarket | Curated Modern Storefront",
  description:
    "Minimalist modern e-commerce storefront featuring curated brands and transparent VND pricing.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className="min-h-screen bg-white text-[#1F1916] antialiased">{children}</body>
    </html>
  );
}
