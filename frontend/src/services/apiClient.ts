import { readAppConfig } from "@/config/envConfig";
import { ApiResponse } from "@/types/api";

const networkErrorMessage =
  "Unable to connect to the server. Please check that the API Gateway is running.";

const parseApiResponse = async <T>(httpResponse: Response): Promise<ApiResponse<T>> => {
  const rawBody = await httpResponse.text();
  if (rawBody.trim().length > 0) {
    try {
      const parsed = JSON.parse(rawBody) as Partial<ApiResponse<T>>;
      return {
        timestamp: parsed.timestamp,
        status: typeof parsed.status === "number" ? parsed.status : httpResponse.status,
        message: parsed.message || `Request failed with HTTP status ${httpResponse.status}.`,
        data: parsed.data ?? null,
      };
    } catch {
      return {
        status: httpResponse.status,
        message: `Request failed with HTTP status ${httpResponse.status}.`,
        data: null,
      };
    }
  }

  return {
    status: httpResponse.status,
    message: `Request failed with HTTP status ${httpResponse.status}.`,
    data: null,
  };
};

/**
 * Sends a GET request through the API Gateway on port 8080 and returns ApiResponse<T>.
 */
export const readGatewayResource = async <T>(
  path: string,
  cacheTags: string[] = [],
  revalidateSeconds: number = 60,
): Promise<ApiResponse<T>> => {
  const { apiGatewayUrl } = readAppConfig();
  const normalizedPath = path.startsWith("/") ? path : `/${path}`;
  const targetUrl = `${apiGatewayUrl}${normalizedPath}`;

  const fetchOptions: RequestInit & { next?: { revalidate: number; tags: string[] } } = {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  };

  if (revalidateSeconds > 0) {
    fetchOptions.next = {
      revalidate: revalidateSeconds,
      tags: cacheTags,
    };
  } else {
    fetchOptions.cache = "no-store";
  }

  try {
    const httpResponse = await fetch(targetUrl, fetchOptions);
    return await parseApiResponse<T>(httpResponse);
  } catch {
    return {
      status: 503,
      message: networkErrorMessage,
      data: null,
    };
  }
};
