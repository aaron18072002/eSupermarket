import { readAppConfig } from "./env-config";

export interface ApiResponse<T> {
  status: number;
  message: string;
  data: T;
}

export class ApiError extends Error {
  public readonly status: number;
  public readonly responseBody?: unknown;

  constructor(status: number, message: string, responseBody?: unknown) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.responseBody = responseBody;
  }
}

const handleResponse = async <T>(response: Response): Promise<T> => {
  if (!response.ok) {
    let errorData: unknown;
    try {
      errorData = await response.json();
    } catch {
      errorData = await response.text();
    }

    const message =
      typeof errorData === "object" &&
      errorData !== null &&
      "message" in errorData
        ? String((errorData as { message: unknown }).message)
        : `Request failed with HTTP status ${response.status}`;

    switch (response.status) {
      case 400:
        throw new ApiError(400, `Bad Request: ${message}`, errorData);
      case 401:
        throw new ApiError(
          401,
          `Unauthorized: Session expired or invalid token`,
          errorData,
        );
      case 403:
        throw new ApiError(
          403,
          `Forbidden: You do not have permission for this resource`,
          errorData,
        );
      case 404:
        throw new ApiError(404, `Not Found: ${message}`, errorData);
      case 409:
        throw new ApiError(
          409,
          `Conflict: Duplicate resource detected`,
          errorData,
        );
      case 500:
      default:
        throw new ApiError(
          response.status,
          `Internal Server Error: ${message}`,
          errorData,
        );
    }
  }

  const envelope: ApiResponse<T> = await response.json();
  return envelope.data;
};

// 1. Read Action (with Next.js ISR cache tags)
export const readResource = async <T>(
  path: string,
  cacheTags: string[] = [],
  revalidateSeconds: number = 60,
): Promise<T> => {
  const { apiGatewayUrl } = readAppConfig();
  const targetUrl = `${apiGatewayUrl}${path.startsWith("/") ? path : `/${path}`}`;

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

  return handleResponse<T>(response);
};

// 2. Search Action
export const searchResources = async <T>(
  path: string,
  queryParams: Record<string, string | number>,
): Promise<T> => {
  const { apiGatewayUrl } = readAppConfig();
  const searchUrl = new URL(
    `${apiGatewayUrl}${path.startsWith("/") ? path : `/${path}`}`,
  );

  Object.entries(queryParams).forEach(([key, value]) => {
    searchUrl.searchParams.append(key, String(value));
  });

  const response = await fetch(searchUrl.toString(), {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
    cache: "no-store",
  });

  return handleResponse<T>(response);
};

// 3. Create Action (mutating)
export const createResource = async <TInput, TOutput>(
  path: string,
  payload: TInput,
  bearerToken?: string,
): Promise<TOutput> => {
  const { apiGatewayUrl } = readAppConfig();
  const targetUrl = `${apiGatewayUrl}${path.startsWith("/") ? path : `/${path}`}`;

  const headers: HeadersInit = {
    "Content-Type": "application/json",
  };
  if (bearerToken) {
    headers["Authorization"] = `Bearer ${bearerToken}`;
  }

  const response = await fetch(targetUrl, {
    method: "POST",
    headers,
    body: JSON.stringify(payload),
  });

  return handleResponse<TOutput>(response);
};

// 4. Update Action (mutating)
export const updateResource = async <TInput, TOutput>(
  path: string,
  payload: TInput,
  bearerToken?: string,
): Promise<TOutput> => {
  const { apiGatewayUrl } = readAppConfig();
  const targetUrl = `${apiGatewayUrl}${path.startsWith("/") ? path : `/${path}`}`;

  const headers: HeadersInit = {
    "Content-Type": "application/json",
  };
  if (bearerToken) {
    headers["Authorization"] = `Bearer ${bearerToken}`;
  }

  const response = await fetch(targetUrl, {
    method: "PUT",
    headers,
    body: JSON.stringify(payload),
  });

  return handleResponse<TOutput>(response);
};

// 5. Delete Action (mutating)
export const deleteResource = async <TOutput>(
  path: string,
  bearerToken?: string,
): Promise<TOutput> => {
  const { apiGatewayUrl } = readAppConfig();
  const targetUrl = `${apiGatewayUrl}${path.startsWith("/") ? path : `/${path}`}`;

  const headers: HeadersInit = {
    "Content-Type": "application/json",
  };
  if (bearerToken) {
    headers["Authorization"] = `Bearer ${bearerToken}`;
  }

  const response = await fetch(targetUrl, {
    method: "DELETE",
    headers,
  });

  return handleResponse<TOutput>(response);
};
