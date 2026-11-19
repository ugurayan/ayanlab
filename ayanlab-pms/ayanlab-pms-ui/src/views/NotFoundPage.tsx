import { Link } from "react-router-dom";
import { Button } from "../ui/components/Button";

export function NotFoundPage() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-4 p-6">
      <h1 className="text-2xl font-bold">Page not found</h1>
      <Link to="/app/dashboard">
        <Button type="button">Back to dashboard</Button>
      </Link>
    </div>
  );
}