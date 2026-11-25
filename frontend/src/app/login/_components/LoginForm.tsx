"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { createAuthLogin, storeAuthTokens } from "@/services/authService";

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

    if (!email.trim() || !password.trim()) {
      setErrorMessage("Please enter both email and password.");
      return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(email.trim())) {
      setErrorMessage("Please enter a valid email address.");
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
        setErrorMessage(response.message || "Invalid email or password.");
      }
    } catch (error) {
      const fallbackMessage =
        error instanceof Error ? error.message : "An unexpected error occurred. Please try again.";
      setErrorMessage(fallbackMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div
      className="w-full max-w-md mx-auto bg-white
        rounded-3xl border border-[#EAE3DB] shadow-[0_12px_40px_rgba(62,39,35,0.06)]
        p-8 sm:p-10"
    >
      {/* Editorial Header */}
      <div className="text-center mb-8 space-y-2">
        <span
          className="inline-block rounded-full bg-[#F5F1EC] px-3 py-1
            text-[10px] font-semibold uppercase tracking-[0.16em] text-[#8B5E3C]"
        >
          Member Portal
        </span>
        <h1 className="font-serif text-2xl sm:text-3xl font-medium text-[#1F1916]">Welcome back</h1>
        <p className="text-xs text-[#6E6259]">
          Sign in to manage your orders, saved essentials, and member offers.
        </p>
      </div>

      {/* Error Alert Banner */}
      {errorMessage && (
        <div
          className="mb-5 p-3.5 bg-[#FEF3F2] border border-red-200 rounded-xl
            text-[#B42318] text-xs flex items-start gap-2.5"
        >
          <svg className="w-4 h-4 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
            <path
              fillRule="evenodd"
              d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
              clipRule="evenodd"
            />
          </svg>
          <span className="font-medium leading-relaxed">{errorMessage}</span>
        </div>
      )}

      {/* Sign In Form */}
      <form onSubmit={(e) => handleSubmit(e)} className="space-y-4">
        <div>
          <label htmlFor="email" className="block text-xs font-medium text-[#5C4033] mb-1.5">
            Email Address
          </label>
          <input
            id="email"
            type="email"
            autoComplete="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="name@example.com"
            className="w-full px-4 py-2.5 text-sm bg-[#FAF8F5]
              border border-[#E5DDD3] rounded-xl text-[#1F1916] placeholder-[#94877C]
              focus:outline-none focus:bg-white focus:border-[#5C4033]
              focus:ring-1 focus:ring-[#5C4033] transition-colors"
          />
        </div>

        <div>
          <div className="flex items-center justify-between mb-1.5">
            <label htmlFor="password" className="block text-xs font-medium text-[#5C4033]">
              Password
            </label>
            <a
              href="/forgot-password"
              className="text-xs text-[#8B5E3C] hover:text-[#3E2723] hover:underline font-medium"
            >
              Forgot password?
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
              placeholder="Enter your password"
              className="w-full px-4 py-2.5 pr-14 text-sm bg-[#FAF8F5]
                border border-[#E5DDD3] rounded-xl text-[#1F1916] placeholder-[#94877C]
                focus:outline-none focus:bg-white focus:border-[#5C4033]
                focus:ring-1 focus:ring-[#5C4033] transition-colors"
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute inset-y-0 right-0 pr-3.5 flex items-center
                text-[#6E6259] hover:text-[#3E2723] text-xs font-medium"
            >
              {showPassword ? "Hide" : "Show"}
            </button>
          </div>
        </div>

        <button
          type="submit"
          disabled={isLoading}
          className="w-full py-3 px-5 mt-2 bg-[#3E2723] hover:bg-[#5C4033] disabled:bg-[#C4B8AD]
            text-white font-medium rounded-full transition-colors
            flex items-center justify-center gap-2 text-xs tracking-wide"
        >
          {isLoading ? (
            <>
              <svg className="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                <circle
                  className="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  strokeWidth="4"
                />
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z" />
              </svg>
              <span>Signing in...</span>
            </>
          ) : (
            <span>Sign In</span>
          )}
        </button>
      </form>

      {/* Footer Registration Link */}
      <div className="mt-7 pt-5 border-t border-[#F3EFEA] text-center">
        <p className="text-xs text-[#6E6259]">
          New to eSupermarket?{" "}
          <a
            href="/register"
            className="text-[#3E2723] font-semibold
              underline underline-offset-4"
          >
            Create an account
          </a>
        </p>
      </div>
    </div>
  );
};
