import { readAppConfig } from "@/config/envConfig";
import { ApiResponse, AuthResponse, LoginRequest, UserResponse } from "@/types/auth";

const parseAuthResponse = async (httpResponse: Response): Promise<ApiResponse<AuthResponse>> => {
  const rawBody = await httpResponse.text();
  if (rawBody.trim().length > 0) {
    try {
      const parsed = JSON.parse(rawBody) as Partial<ApiResponse<AuthResponse>>;
      return {
        timestamp: parsed.timestamp,
        status: typeof parsed.status === "number" ? parsed.status : httpResponse.status,
        message:
          parsed.message ||
          `Authentication request failed with HTTP status ${httpResponse.status}.`,
        data: parsed.data ?? null,
      };
    } catch {
      return {
        status: httpResponse.status,
        message: `Authentication request failed with HTTP status ${httpResponse.status}.`,
        data: null,
      };
    }
  }

  return {
    status: httpResponse.status,
    message: `Authentication request failed with HTTP status ${httpResponse.status}.`,
    data: null,
  };
};

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

    return await parseAuthResponse(httpResponse);
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
  const rawProfile = localStorage.getItem("userProfile");
  if (!rawProfile) {
    return null;
  }
  try {
    return JSON.parse(rawProfile) as UserResponse;
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
