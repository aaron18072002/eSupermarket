"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { createAuthLogin, storeAuthTokens } from "@/services/auth-service";

export interface LoginFormProps {
  redirectUrl?: string;
}

export const LoginForm = ({ redirectUrl = "/" }: LoginFormProps) => {
  const router = useRouter();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setErrorMessage(null);

    // Client-side quick field validation
    if (!email.trim() || !password.trim()) {
      setErrorMessage("Vui lòng điền đầy đủ email và mật khẩu.");
      return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(email.trim())) {
      setErrorMessage("Địa chỉ email không đúng định dạng.");
      return;
    }

    setIsLoading(true);

    try {
      const response = await createAuthLogin({
        email: email.trim(),
        password: password,
      });

      if (response.status === 200 && response.data) {
        storeAuthTokens(response.data);
        const destination = redirectUrl.startsWith("/") ? redirectUrl : `/${redirectUrl}`;
        router.push(destination);
        router.refresh();
      } else {
        setErrorMessage(response.message || "Email hoặc mật khẩu không chính xác.");
      }
    } catch {
      setErrorMessage("Có lỗi bất ngờ xảy ra. Vui lòng thử lại sau.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="w-full max-w-md mx-auto bg-white rounded-lg border border-gray-200 shadow-md p-6 sm:p-8">
      {/* Brand Header & Trust Badge */}
      <div className="text-center mb-6">
        <div className="inline-block bg-[#FFD400] text-gray-900 text-xs font-bold px-2.5 py-1 rounded mb-2">
          HỆ THỐNG SIÊU THỊ ĐIỆN MÁY
        </div>
        <h1 className="text-2xl font-bold text-gray-900">Đăng nhập tài khoản</h1>
        <p className="text-xs text-gray-500 mt-1">Nhập thông tin tài khoản để quản lý đơn hàng & tích lũy điểm</p>
      </div>

      {/* Error Alert Banner */}
      {errorMessage && (
        <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-md text-[#D0021B] text-sm flex items-start gap-2">
          <svg className="w-5 h-5 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
            <path
              fillRule="evenodd"
              d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
              clipRule="evenodd"
            />
          </svg>
          <span className="font-medium">{errorMessage}</span>
        </div>
      )}

      {/* Login Form */}
      <form onSubmit={(e) => handleSubmit(e)} className="space-y-4">
        {/* Email Field */}
        <div>
          <label htmlFor="email" className="block text-sm font-semibold text-gray-700 mb-1">
            Địa chỉ Email <span className="text-[#D0021B]">*</span>
          </label>
          <input
            id="email"
            type="email"
            autoComplete="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="nguyenvana@gmail.com"
            className="w-full px-3.5 py-2.5 text-sm bg-white border border-gray-300 rounded-md text-gray-900 placeholder-gray-400 focus:outline-none focus:border-[#2A83E9] focus:ring-1 focus:ring-[#2A83E9] transition"
          />
        </div>

        {/* Password Field */}
        <div>
          <div className="flex items-center justify-between mb-1">
            <label htmlFor="password" className="block text-sm font-semibold text-gray-700">
              Mật khẩu <span className="text-[#D0021B]">*</span>
            </label>
            <a href="/forgot-password" className="text-xs text-[#2A83E9] hover:underline font-medium">
              Quên mật khẩu?
            </a>
          </div>
          <div className="relative">
            <input
              id="password"
              type={showPassword ? "text" : "password"}
              autoComplete="current-password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              className="w-full px-3.5 py-2.5 pr-10 text-sm bg-white border border-gray-300 rounded-md text-gray-900 placeholder-gray-400 focus:outline-none focus:border-[#2A83E9] focus:ring-1 focus:ring-[#2A83E9] transition"
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600 text-xs font-medium"
            >
              {showPassword ? "Ẩn" : "Hiện"}
            </button>
          </div>
        </div>

        {/* Conversion Orange Submit Button */}
        <button
          type="submit"
          disabled={isLoading}
          className="w-full py-3 px-4 bg-[#FF6600] hover:bg-[#E65C00] disabled:bg-gray-400 text-white font-bold rounded-md shadow transition duration-150 flex items-center justify-center gap-2 text-base"
        >
          {isLoading ? (
            <>
              <svg className="animate-spin h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
              </svg>
              <span>Đang xử lý...</span>
            </>
          ) : (
            <span>ĐĂNG NHẬP</span>
          )}
        </button>
      </form>

      {/* Footer Registration Link */}
      <div className="mt-6 pt-5 border-t border-gray-100 text-center">
        <p className="text-xs text-gray-600">
          Chưa có tài khoản tại eSupermarket?{" "}
          <a href="/register" className="text-[#2A83E9] font-bold hover:underline">
            Đăng ký ngay
          </a>
        </p>
      </div>
    </div>
  );
};
