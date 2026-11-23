import type { Metadata } from "next";
import Link from "next/link";
import { LoginForm } from "./_components/LoginForm";

export const metadata: Metadata = {
  title: "Đăng nhập tài khoản | eSupermarket",
  description: "Đăng nhập vào hệ thống siêu thị eSupermarket để quản lý giỏ hàng, đơn hàng và ưu đãi.",
};

interface LoginPageProps {
  searchParams: {
    redirect?: string;
  };
}

export default function LoginPage({ searchParams }: LoginPageProps) {
  const redirectUrl = searchParams.redirect || "/";

  return (
    <div className="min-h-screen flex flex-col bg-[#F3F4F6]">
      {/* Brand Electric Blue Header */}
      <header className="sticky top-0 z-50 bg-[#2A83E9] text-white shadow-md">
        <div className="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">
          <Link href="/" className="flex items-center gap-2">
            <span className="font-extrabold text-xl tracking-tight text-white uppercase">eSupermarket</span>
            <span className="bg-[#FFD400] text-gray-900 text-[10px] font-bold px-1.5 py-0.5 rounded">ONLINE</span>
          </Link>
          <div className="text-xs text-white/90 hidden sm:block">
            Tổng đài hỗ trợ: <strong className="text-[#FFD400]">1800 1061</strong> (7:30 - 22:00)
          </div>
        </div>
      </header>

      {/* Main Content Form Area */}
      <main className="flex-1 flex flex-col justify-center items-center py-10 px-4">
        <LoginForm redirectUrl={redirectUrl} />

        {/* Brand Reassurance Badges */}
        <div className="mt-8 flex flex-wrap justify-center items-center gap-4 text-xs text-gray-500">
          <span className="flex items-center gap-1.5">
            <span className="w-2 h-2 rounded-full bg-[#2A83E9]"></span>
            100% Hàng chính hãng
          </span>
          <span className="flex items-center gap-1.5">
            <span className="w-2 h-2 rounded-full bg-[#2A83E9]"></span>
            Bảo hành chính hãng 12 tháng
          </span>
          <span className="flex items-center gap-1.5">
            <span className="w-2 h-2 rounded-full bg-[#2A83E9]"></span>
            Giao hàng siêu tốc 2 giờ
          </span>
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-white border-t border-gray-200 py-4 text-center text-xs text-gray-500">
        <p>© 2026 eSupermarket. Tất cả quyền được bảo lưu.</p>
      </footer>
    </div>
  );
}
