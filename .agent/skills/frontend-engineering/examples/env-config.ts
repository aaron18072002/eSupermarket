/**
 * Strict Fail-Fast Environment Configuration
 *
 * Enforces immediate startup failure if any required environment variable is missing.
 * STRICTLY FORBIDDEN: Default fallbacks like process.env.URL || "http://localhost:8080".
 */

const getRequiredEnv = (key: string): string => {
  const value = process.env[key];
  if (!value || value.trim().length === 0) {
    throw new Error(
      `[FAIL-FAST CONFIG ERROR] Missing required environment variable: "${key}". ` +
        `Check your .env.local file. Inline default fallbacks are strictly prohibited.`,
    );
  }
  return value.trim();
};

export interface AppConfig {
  apiGatewayUrl: string;
}

export const readAppConfig = (): AppConfig => {
  return {
    // Both server and client must access the API Gateway on port 8080
    apiGatewayUrl: getRequiredEnv("NEXT_PUBLIC_API_GATEWAY_URL"),
  };
};
