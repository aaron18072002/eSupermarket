/**
 * Strict Fail-Fast Environment Configuration
 *
 * Enforces immediate startup failure if any required environment variable is missing.
 * Uses static process.env.NEXT_PUBLIC_* reads so Next.js inlines values in client bundles.
 * Strictly forbidden: default fallbacks like process.env.URL || "http://localhost:8080".
 */

const assertRequiredEnv = (key: string, value: string | undefined): string => {
  if (!value || value.trim().length === 0) {
    throw new Error(
      `Missing required environment variable: "${key}". ` +
        "Check your .env.local file. Inline default fallbacks are strictly prohibited.",
    );
  }
  return value.trim();
};

export interface AppConfig {
  apiGatewayUrl: string;
}

export const readAppConfig = (): AppConfig => {
  return {
    apiGatewayUrl: assertRequiredEnv(
      "NEXT_PUBLIC_API_GATEWAY_URL",
      process.env.NEXT_PUBLIC_API_GATEWAY_URL,
    ),
  };
};
