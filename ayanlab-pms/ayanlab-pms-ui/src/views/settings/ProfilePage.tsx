import { useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../../state/auth";
import { useLocale } from "../../state/locale";
import { PageHeader } from "../../ui/components/PageHeader";
import { Card, CardContent } from "../../ui/components/Card";
import { Input } from "../../ui/components/Input";
import { Button } from "../../ui/components/Button";
import { Badge } from "../../ui/components/Badge";
import { Label } from "../../ui/components/Label";

export function ProfilePage() {
  const { user } = useAuth();
  const { t } = useLocale();
  const [name, setName] = useState(user?.displayName ?? "");

  if (!user) return null;

  return (
    <div className="mx-auto max-w-2xl space-y-4">
      <PageHeader title={t("profile.title")} subtitle={t("profile.subtitle")} />
      <Card>
        <CardContent className="space-y-4 text-sm">
          <div>
            <Label htmlFor="profile-name">{t("profile.displayName")}</Label>
            <Input id="profile-name" value={name} onChange={(e) => setName(e.target.value)} />
          </div>
          <div>
            <Label htmlFor="profile-email">{t("profile.email")}</Label>
            <Input id="profile-email" value={user.email} readOnly className="bg-[rgb(var(--surface-muted))]" />
          </div>
          <div>
            <Label>{t("profile.roles")}</Label>
            <div className="flex flex-wrap gap-2">
              {user.roles.map((r) => (
                <Badge key={r} tone="neutral">
                  {r}
                </Badge>
              ))}
            </div>
          </div>
          <div>
            <Label htmlFor="profile-org">{t("profile.organization")}</Label>
            <Input id="profile-org" value={user.orgName} readOnly className="bg-[rgb(var(--surface-muted))]" />
          </div>
          <p className="text-xs text-[rgb(var(--text-muted))]">{t("profile.passwordHint")}</p>
          <div className="flex flex-wrap gap-2">
            <Button type="button" onClick={() => alert(t("profile.updateMock"))}>
              {t("profile.updateMock")}
            </Button>
            <Link to="/app/settings/application">
              <Button type="button" variant="secondary">
                {t("nav.application")}
              </Button>
            </Link>
            <Link to="/app/legal/account-deletion">
              <Button type="button" variant="ghost">
                {t("nav.accountDeletion")}
              </Button>
            </Link>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}