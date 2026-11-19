import { useEffect, type ReactNode } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useLease, useSaveLease } from "../../data/hooks";
import { db } from "../../mock/mockDb";
import { PageHeader } from "../../ui/components/PageHeader";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";
import { Card, CardContent } from "../../ui/components/Card";
import { Label } from "../../ui/components/Label";
import { Select } from "../../ui/components/Select";
import { loadingLineClass } from "../../lib/uiClasses";

const schema = z.object({
  unitId: z.string().min(1),
  tenantId: z.string().min(1),
  startDate: z.string().min(1),
  endDate: z.string().min(1),
  rentCents: z.coerce.number().min(0),
  status: z.enum(["DRAFT", "ACTIVE", "EXPIRING", "ENDED"])
});

type Form = z.infer<typeof schema>;

export function LeaseEditorPage() {
  const { id } = useParams();
  const isNew = id === "new" || !id;
  const navigate = useNavigate();
  const { data: lease, isLoading } = useLease(isNew ? undefined : id);
  const save = useSaveLease();

  const form = useForm<Form>({
    resolver: zodResolver(schema),
    defaultValues: {
      unitId: db.units.find((u) => u.status === "VACANT")?.id ?? db.units[0].id,
      tenantId: db.tenants[0].id,
      startDate: new Date().toISOString().slice(0, 10),
      endDate: new Date(Date.now() + 365 * 86400000).toISOString().slice(0, 10),
      rentCents: 150000,
      status: "DRAFT"
    }
  });

  useEffect(() => {
    if (lease && !isNew) {
      form.reset({
        unitId: lease.unitId,
        tenantId: lease.tenantId,
        startDate: lease.startDate.slice(0, 10),
        endDate: lease.endDate.slice(0, 10),
        rentCents: lease.rentCents,
        status: lease.status
      });
    }
  }, [lease, form, isNew]);

  async function onSubmit(values: Form) {
    const saved = await save.mutateAsync({ id: isNew ? "new" : id!, input: values });
    navigate(`/app/leasing/leases/${saved.id}`, { replace: true });
  }

  if (!isNew && isLoading) return <p className={loadingLineClass}>Loading…</p>;

  return (
    <div>
      <PageHeader
        title={isNew ? "New lease" : `Lease ${id}`}
        subtitle={lease ? `${lease.propertyName} · ${lease.unitLabel}` : "Create or update lease terms (mock save)."}
        actions={
          <Link to="/app/leasing/leases" className="text-sm text-teal-800 hover:underline">
            Back
          </Link>
        }
      />
      <Card>
        <CardContent>
          <form className="mx-auto max-w-lg space-y-4" onSubmit={form.handleSubmit(onSubmit)}>
            <Field label="Unit" htmlFor="lease-unit">
              <Select id="lease-unit" {...form.register("unitId")}>
                {db.units.map((u) => (
                  <option key={u.id} value={u.id}>
                    {u.label} ({u.status})
                  </option>
                ))}
              </Select>
            </Field>
            <Field label="Tenant" htmlFor="lease-tenant">
              <Select id="lease-tenant" {...form.register("tenantId")}>
                {db.tenants.map((t) => (
                  <option key={t.id} value={t.id}>
                    {t.displayName}
                  </option>
                ))}
              </Select>
            </Field>
            <div className="grid gap-4 sm:grid-cols-2">
              <Field label="Start">
                <Input type="date" {...form.register("startDate")} />
              </Field>
              <Field label="End">
                <Input type="date" {...form.register("endDate")} />
              </Field>
            </div>
            <Field label="Monthly rent (cents)">
              <Input type="number" {...form.register("rentCents")} />
            </Field>
            <Field label="Status" htmlFor="lease-status">
              <Select id="lease-status" {...form.register("status")}>
                {(["DRAFT", "ACTIVE", "EXPIRING", "ENDED"] as const).map((s) => (
                  <option key={s} value={s}>
                    {s}
                  </option>
                ))}
              </Select>
            </Field>
            <Button type="submit" isLoading={save.isPending}>
              Save lease
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}

function Field({
  label,
  htmlFor,
  children
}: {
  label: string;
  htmlFor?: string;
  children: ReactNode;
}) {
  return (
    <div>
      <Label htmlFor={htmlFor}>{label}</Label>
      {children}
    </div>
  );
}