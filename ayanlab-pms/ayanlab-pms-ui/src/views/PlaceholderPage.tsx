import { Card, CardContent, CardHeader, CardTitle } from "../ui/components/Card";
import { Badge } from "../ui/components/Badge";

export function PlaceholderPage({ title, hint }: { title: string; hint?: string }) {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">{title}</CardTitle>
      </CardHeader>
      <CardContent className="space-y-3 text-sm text-[rgb(var(--text-muted))]">
        <Badge tone="neutral">Mock shell — M0</Badge>
        <p>{hint ?? "Screen layout and forms will be filled in the next M0 sprint tasks."}</p>
      </CardContent>
    </Card>
  );
}