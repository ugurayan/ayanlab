import { Moon, Sun } from "lucide-react";
import { useTheme } from "../../state/theme";
import { Button } from "./Button";

export function ThemeToggle({ className }: { className?: string }) {
  const { resolved, toggle } = useTheme();
  const isDark = resolved === "dark";
  return (
    <Button
      type="button"
      variant="ghost"
      size="sm"
      className={className}
      onClick={toggle}
      title={isDark ? "Light mode" : "Dark mode"}
      aria-label={isDark ? "Switch to light mode" : "Switch to dark mode"}
    >
      {isDark ? <Sun size={16} /> : <Moon size={16} />}
    </Button>
  );
}