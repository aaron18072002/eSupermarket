import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "var(--background)",
        foreground: "var(--foreground)",
        brand: {
          espresso: "#3E2723",
          cocoa: "#5C4033",
          chestnut: "#8B5E3C",
          sand: "#F5F1EC",
          alabaster: "#FAF8F5",
          border: "#EAE3DB",
          ink: "#1F1916",
          muted: "#6E6259",
        },
      },
    },
  },
  plugins: [],
};
export default config;
