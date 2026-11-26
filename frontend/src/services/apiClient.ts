import { readAppConfig } from "@/config/envConfig";
import { ApiResponse } from "@/types/api";

const networkErrorMessage =
  "Unable to connect to the server. Please check that the API Gateway is running.";

/**
 * Converts a Fetch Response into the uniform ApiResponse<T> envelope.
 */
export const convertApiResponse = async <T>(
  httpResponse: Response,
  actionLabel: string = "Request",
): Promise<ApiResponse<T>> => {
  const fallbackMessage = `${actionLabel} failed with HTTP status ${httpResponse.status}.`;
  const body = await httpResponse.text();
  if (body.trim().length === 0) {
    return {
      status: httpResponse.status,
      message: fallbackMessage,
      data: null,
    };
  }

  try {
    const envelope = JSON.parse(body) as Partial<ApiResponse<T>> | null;
    if (!envelope || typeof envelope !== "object") {
      return {
        status: httpResponse.status,
        message: fallbackMessage,
        data: null,
      };
    }
    return {
      timestamp: envelope.timestamp,
      status: typeof envelope.status === "number" ? envelope.status : httpResponse.status,
      message: envelope.message || fallbackMessage,
      data: envelope.data ?? null,
    };
  } catch {
    return {
      status: httpResponse.status,
      message: fallbackMessage,
      data: null,
    };
  }
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
    return await convertApiResponse<T>(httpResponse);
  } catch {
    return {
      status: 503,
      message: networkErrorMessage,
      data: null,
    };
  }
};
