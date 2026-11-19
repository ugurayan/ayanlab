import { useEffect, useState } from "react";
import { useResidentBranding, useSaveResidentBranding } from "../../data/hooks";
import { PageHeader } from "../../ui/components/PageHeader";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";
import { Card, CardContent } from "../../ui/components/Card";
import { Label } from "../../ui/components/Label";

export function ResidentCenterPage() {
  const { data } = useResidentBranding();
  const save = useSaveResidentBranding();
  const [primaryColor, setPrimaryColor] = useState("#0f766e");
  const [showPayments, setShowPayments] = useState(true);
  const [showMaintenance, setShowMaintenance] = useState(true);
  const [showDocuments, setShowDocuments] = useState(true);

  useEffect(() => {
    if (data) {
      setPrimaryColor(data.primaryColor);
      setShowPayments(data.showPayments);
      setShowMaintenance(data.showMaintenance);
      setShowDocuments(data.showDocuments);
    }
  }, [data]);

  return (
    <div>
      <PageHeader title="Resident Center" subtitle="Branding and visible portal pages (mock)." />
      <Card>
        <CardContent className="mx-auto max-w-lg space-y-4">
          <Field label="Primary color">
            <Input type="color" value={primaryColor} onChange={(e) => setPrimaryColor(e.target.value)} className="h-10 w-20" />
          </Field>
          <Toggle label="Show payments" checked={showPayments} onChange={setShowPayments} />
          <Toggle label="Show maintenance" checked={showMaintenance} onChange={setShowMaintenance} />
          <Toggle label="Show documents" checked={showDocuments} onChange={setShowDocuments} />
          <div
            className="rounded-xl border border-[rgb(var(--border))] bg-[rgb(var(--surface-muted))] p-4 text-sm"
            style={{ borderColor: primaryColor }}
          >
            Preview accent border uses selected color.
          </div>
          <Button
            type="button"
            onClick={() =>
              save.mutate({ primaryColor, showPayments, showMaintenance, showDocuments })
            }
            isLoading={save.isPending}
          >
            Save settings
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div>
      <Label>{label}</Label>
      {children}
    </div>
  );
}

function Toggle({ label, checked, onChange }: { label: string; checked: boolean; onChange: (v: boolean) => void }) {
  return (
    <label className="flex items-center justify-between text-sm">
      {label}
      <input type="checkbox" checked={checked} onChange={(e) => onChange(e.target.checked)} />
    </label>
  );
}