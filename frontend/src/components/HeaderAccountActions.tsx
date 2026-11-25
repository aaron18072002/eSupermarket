"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { deleteAuthTokens, readAuthToken, readAuthUserProfile } from "@/services/authService";
import { UserResponse } from "@/types/auth";

export const HeaderAccountActions = () => {
  const router = useRouter();
  const [userProfile, setUserProfile] = useState<UserResponse | null>(null);

  useEffect(() => {
    const token = readAuthToken();
    const profile = readAuthUserProfile();
    if (token && profile) {
      setUserProfile(profile);
    }
  }, []);

  const handleLogout = () => {
    deleteAuthTokens();
    setUserProfile(null);
    router.refresh();
  };

  if (userProfile) {
    return (
      <div className="flex items-center gap-3">
        <span className="hidden md:inline-block text-xs text-[#6E6259] font-medium">
          {userProfile.fullName}
        </span>
        <button
          type="button"
          onClick={() => handleLogout()}
          className="px-4 py-2 rounded-full text-xs font-medium
            border border-[#D8CFC4] text-[#3E2723] bg-[#FAF8F5]
            hover:bg-[#3E2723] hover:text-white transition-colors"
        >
          Sign Out
        </button>
      </div>
    );
  }

  return (
    <Link
      href="/login"
      className="px-5 py-2 rounded-full text-xs font-medium
        bg-[#3E2723] text-white hover:bg-[#5C4033] transition-colors"
    >
      Sign In
    </Link>
  );
};
