import { type ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useCreateProperty } from "../../data/hooks";
import { PageHeader } from "../../ui/components/PageHeader";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";
import { Card, CardContent } from "../../ui/components/Card";
import { Label } from "../../ui/components/Label";
import type { PropertyMode } from "../../types/pms";

const schema = z.object({
  name: z.string().min(2),
  address: z.string().min(4),
  mode: z.enum(["RENTAL", "ASSOCIATION"]),
  unitCount: z.coerce.number().min(1)
});

type Form = z.infer<typeof schema>;

export function PropertyCreatePage() {
  const navigate = useNavigate();
  const create = useCreateProperty();
  const form = useForm<Form>({
    resolver: zodResolver(schema),
    defaultValues: { name: "", address: "", mode: "RENTAL", unitCount: 1 }
  });

  async function onSubmit(values: Form) {
    const p = await create.mutateAsync(values);
    navigate(`/app/rentals/properties/${p.id}`);
  }

  return (
    <div>
      <PageHeader title="New property" subtitle="Two-step mock wizard (save to local mock DB)." />
      <Card>
        <CardContent className="mx-auto max-w-lg space-y-4">
          <Field label="Name">
            <Input {...form.register("name")} />
          </Field>
          <Field label="Address">
            <Input {...form.register("address")} />
          </Field>
          <Field label="Mode">
            <select className="w-full rounded-xl border px-3 py-2 text-sm" {...form.register("mode")}>
              {(["RENTAL", "ASSOCIATION"] as PropertyMode[]).map((m) => (
                <option key={m} value={m}>{m}</option>
              ))}
            </select>
          </Field>
          <Field label="Unit count">
            <Input type="number" {...form.register("unitCount")} />
          </Field>
          <Button type="button" onClick={form.handleSubmit(onSubmit)} isLoading={create.isPending}>
            Create property
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}

function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div>
      <Label>{label}</Label>
      {children}
    </div>
  );
}