import { readAppConfig } from "@/config/envConfig";
import { convertApiResponse } from "@/services/apiClient";
import { ApiResponse, AuthResponse, LoginRequest, UserResponse } from "@/types/auth";

/**
 * Sends login credentials to the API Gateway on port 8080 (/api/v1/auth/login).
 * Pure functional approach returning the uniform ApiResponse<AuthResponse> directly.
 */
export const createAuthLogin = async (
  payload: LoginRequest,
): Promise<ApiResponse<AuthResponse>> => {
  const { apiGatewayUrl } = readAppConfig();
  const endpoint = `${apiGatewayUrl}/api/v1/auth/login`;

  try {
    const httpResponse = await fetch(endpoint, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    return await convertApiResponse<AuthResponse>(httpResponse, "Authentication request");
  } catch {
    return {
      status: 503,
      message: "Unable to reach the server. Please check that the API Gateway is running.",
      data: null,
    };
  }
};

/**
 * Stores authentication tokens and user profile in browser localStorage.
 */
export const storeAuthTokens = (authData: AuthResponse): void => {
  if (typeof window === "undefined") {
    return;
  }
  localStorage.setItem("accessToken", authData.accessToken);
  localStorage.setItem("refreshToken", authData.refreshToken);
  localStorage.setItem("tokenType", authData.tokenType);
  localStorage.setItem("userProfile", JSON.stringify(authData.user));
};

/**
 * Retrieves the stored JWT access token from localStorage.
 */
export const readAuthToken = (): string | null => {
  if (typeof window === "undefined") {
    return null;
  }
  return localStorage.getItem("accessToken");
};

/**
 * Retrieves the stored user profile from localStorage.
 */
export const readAuthUserProfile = (): UserResponse | null => {
  if (typeof window === "undefined") {
    return null;
  }
  const profile = localStorage.getItem("userProfile");
  if (!profile) {
    return null;
  }
  try {
    return JSON.parse(profile) as UserResponse;
  } catch {
    return null;
  }
};

/**
 * Removes all authentication tokens and session data from localStorage.
 */
export const deleteAuthTokens = (): void => {
  if (typeof window === "undefined") {
    return;
  }
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("tokenType");
  localStorage.removeItem("userProfile");
};
