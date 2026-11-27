/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        brand: {
          DEFAULT: "#E2186F",
          hover: "#C4125C",
          soft: "#FCE8F1",
          ring: "#F9D0E3"
        },
        ink: {
          DEFAULT: "#161616",
          muted: "#71717A",
          subtle: "#A1A1AA"
        },
        line: "#E4E4E7",
        surface: "#F6F6F8",
        canvas: "#FFFFFF"
      },
      fontFamily: {
        sans: [
          "Inter",
          "ui-sans-serif",
          "system-ui",
          "-apple-system",
          "Segoe UI",
          "Roboto",
          "Helvetica Neue",
          "Arial",
          "sans-serif"
        ]
      },
      boxShadow: {
        soft: "0 10px 30px rgba(0,0,0,0.08)",
        card: "0 1px 2px rgba(22, 22, 22, 0.06), 0 4px 14px rgba(22, 22, 22, 0.04)",
        panel: "0 8px 24px rgba(22, 22, 22, 0.08)"
      },
      borderRadius: {
        ui: "12px"
      }
    }
  },
  plugins: []
};
