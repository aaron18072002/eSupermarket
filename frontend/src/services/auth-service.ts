import { readAppConfig } from "@/config/env-config";
import { ApiResponse, AuthResponse, LoginRequest } from "@/types/auth";

/**
 * Sends login credentials to the API Gateway on port 8080 (/api/v1/auth/login).
 * Pure functional approach returning the uniform ApiResponse<AuthResponse> directly.
 */
export const createAuthLogin = async (payload: LoginRequest): Promise<ApiResponse<AuthResponse>> => {
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

    const responseBody = (await httpResponse.json()) as ApiResponse<AuthResponse>;
    return responseBody;
  } catch {
    return {
      status: 503,
      message: "Không thể kết nối đến máy chủ. Vui lòng kiểm tra lại kết nối mạng.",
      data: null,
    };
  }
};

/**
 * Stores authentication tokens and user profile securely in browser localStorage.
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
