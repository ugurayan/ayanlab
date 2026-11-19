import { Suspense } from "react";
import { RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { router } from "./router";
import { AuthProvider } from "./state/auth";
import { LiteModeProvider } from "./state/liteMode";
import { LocaleProvider } from "./state/locale";
import { ThemeProvider } from "./state/theme";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { retry: 0, refetchOnWindowFocus: false }
  }
});

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider>
        <LocaleProvider>
          <LiteModeProvider>
            <AuthProvider>
              <Suspense fallback={<div className="p-8 text-sm text-[rgb(var(--text-muted))]">Loading…</div>}>
                <RouterProvider router={router} />
              </Suspense>
            </AuthProvider>
          </LiteModeProvider>
        </LocaleProvider>
      </ThemeProvider>
    </QueryClientProvider>
  );
}