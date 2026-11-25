import { readAppConfig } from "./envConfig";

export interface ApiResponse<T> {
  timestamp?: string;
  status: number;
  message: string;
  data: T | null;
}

export interface AuthSessionData {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: unknown;
}

const networkErrorMessage =
  "Unable to connect to the server. Please check that the API Gateway is running.";

const parseResponse = async <T>(response: Response): Promise<ApiResponse<T>> => {
  const rawBody = await response.text();
  if (rawBody.trim().length > 0) {
    try {
      const parsed = JSON.parse(rawBody) as Partial<ApiResponse<T>>;
      return {
        timestamp: parsed.timestamp,
        status: typeof parsed.status === "number" ? parsed.status : response.status,
        message: parsed.message || `Request failed with HTTP status ${response.status}.`,
        data: parsed.data ?? null,
      };
    } catch {
      return {
        status: response.status,
        message: `Request failed with HTTP status ${response.status}.`,
        data: null,
      };
    }
  }

  return {
    status: response.status,
    message: `Request failed with HTTP status ${response.status}.`,
    data: null,
  };
};

/**
 * 1. Read Action (with Next.js ISR cache tags)
 * Returns ApiResponse<T> directly using pure functional execution.
 */
export const readResource = async <T>(
  path: string,
  cacheTags: string[] = [],
  revalidateSeconds: number = 60,
): Promise<ApiResponse<T>> => {
  const { apiGatewayUrl } = readAppConfig();
  const targetUrl = `${apiGatewayUrl}${path.startsWith("/") ? path : `/${path}`}`;

  try {
    const response = await fetch(targetUrl, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      next: {
        revalidate: revalidateSeconds,
        tags: cacheTags,
      },
    });

    return await parseResponse<T>(response);
  } catch {
    return {
      status: 503,
      message: networkErrorMessage,
      data: null,
    };
  }
};

/**
 * 2. Search Action
 */
export const searchResources = async <T>(
  path: string,
  queryParams: Record<string, string | number>,
): Promise<ApiResponse<T>> => {
  const { apiGatewayUrl } = readAppConfig();
  const searchUrl = new URL(`${apiGatewayUrl}${path.startsWith("/") ? path : `/${path}`}`);

  Object.entries(queryParams).forEach(([key, value]) => {
    searchUrl.searchParams.append(key, String(value));
  });

  try {
    const response = await fetch(searchUrl.toString(), {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
      cache: "no-store",
    });

    return await parseResponse<T>(response);
  } catch {
    return {
      status: 503,
      message: networkErrorMessage,
      data: null,
    };
  }
};

/**
 * 3. Create Action (mutating)
 */
export const createResource = async <TInput, TOutput>(
  path: string,
  payload: TInput,
  bearerToken?: string,
): Promise<ApiResponse<TOutput>> => {
  const { apiGatewayUrl } = readAppConfig();
  const targetUrl = `${apiGatewayUrl}${path.startsWith("/") ? path : `/${path}`}`;

  const headers: HeadersInit = {
    "Content-Type": "application/json",
  };
  if (bearerToken) {
    headers["Authorization"] = `Bearer ${bearerToken}`;
  }

  try {
    const response = await fetch(targetUrl, {
      method: "POST",
      headers,
      body: JSON.stringify(payload),
    });

    return await parseResponse<TOutput>(response);
  } catch {
    return {
      status: 503,
      message: networkErrorMessage,
      data: null,
    };
  }
};

/**
 * 4. Update Action (mutating)
 */
export const updateResource = async <TInput, TOutput>(
  path: string,
  payload: TInput,
  bearerToken?: string,
): Promise<ApiResponse<TOutput>> => {
  const { apiGatewayUrl } = readAppConfig();
  const targetUrl = `${apiGatewayUrl}${path.startsWith("/") ? path : `/${path}`}`;

  const headers: HeadersInit = {
    "Content-Type": "application/json",
  };
  if (bearerToken) {
    headers["Authorization"] = `Bearer ${bearerToken}`;
  }

  try {
    const response = await fetch(targetUrl, {
      method: "PUT",
      headers,
      body: JSON.stringify(payload),
    });

    return await parseResponse<TOutput>(response);
  } catch {
    return {
      status: 503,
      message: networkErrorMessage,
      data: null,
    };
  }
};

/**
 * 5. Delete Action (mutating)
 */
export const deleteResource = async <TOutput>(
  path: string,
  bearerToken?: string,
): Promise<ApiResponse<TOutput>> => {
  const { apiGatewayUrl } = readAppConfig();
  const targetUrl = `${apiGatewayUrl}${path.startsWith("/") ? path : `/${path}`}`;

  const headers: HeadersInit = {
    "Content-Type": "application/json",
  };
  if (bearerToken) {
    headers["Authorization"] = `Bearer ${bearerToken}`;
  }

  try {
    const response = await fetch(targetUrl, {
      method: "DELETE",
      headers,
    });

    return await parseResponse<TOutput>(response);
  } catch {
    return {
      status: 503,
      message: networkErrorMessage,
      data: null,
    };
  }
};

/**
 * 6. Client Session Storage (localStorage)
 */
export const storeAuthTokens = (authData: AuthSessionData): void => {
  if (typeof window === "undefined") {
    return;
  }
  localStorage.setItem("accessToken", authData.accessToken);
  localStorage.setItem("refreshToken", authData.refreshToken);
  localStorage.setItem("tokenType", authData.tokenType);
  localStorage.setItem("userProfile", JSON.stringify(authData.user));
};

export const readAuthToken = (): string | null => {
  if (typeof window === "undefined") {
    return null;
  }
  return localStorage.getItem("accessToken");
};

export const deleteAuthTokens = (): void => {
  if (typeof window === "undefined") {
    return;
  }
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("tokenType");
  localStorage.removeItem("userProfile");
};
