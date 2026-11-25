import type { Metadata } from "next";
import Link from "next/link";
import { LoginForm } from "./_components/LoginForm";

export const metadata: Metadata = {
  title: "Sign In | eSupermarket",
  description: "Sign in to your eSupermarket member account.",
};

interface LoginPageProps {
  searchParams: {
    redirect?: string;
  };
}

export default function LoginPage({ searchParams }: LoginPageProps) {
  const redirectUrl = searchParams.redirect || "/";

  return (
    <div className="min-h-screen flex flex-col bg-[#FAF8F5]">
      {/* Minimalist White Header */}
      <header className="sticky top-0 z-50 bg-white/95 backdrop-blur border-b border-[#EAE3DB]">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
          <Link href="/" className="flex items-center gap-2.5">
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
          <Link
            href="/"
            className="text-xs font-medium text-[#5C4033] hover:text-[#1F1916] transition-colors"
          >
            ← Back to Store
          </Link>
        </div>
      </header>

      {/* Main Centered Editorial Login Card */}
      <main className="flex-1 flex flex-col justify-center items-center py-12 px-4">
        <LoginForm redirectUrl={redirectUrl} />

        {/* Subtle Reassurance Footer Row */}
        <div
          className="mt-8 flex flex-wrap justify-center items-center
            gap-6 text-[11px] uppercase tracking-wider text-[#6E6259]"
        >
          <span>Authentic Brands</span>
          <span>•</span>
          <span>30-Day Returns</span>
          <span>•</span>
          <span>Same-Day Delivery</span>
        </div>
      </main>

      <footer
        className="bg-white border-t border-[#EAE3DB]
          py-4 text-center text-[11px] text-[#6E6259]"
      >
        <p>© 2026 eSupermarket. All rights reserved.</p>
      </footer>
    </div>
  );
}
