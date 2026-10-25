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
        dmx: {
          blue: "#2A83E9",
          yellow: "#FFD400",
          red: "#D0021B",
          orange: "#FF6600",
          gray: "#9E9E9E",
          surface: "#F3F4F6",
        },
      },
    },
  },
  plugins: [],
};
export default config;
