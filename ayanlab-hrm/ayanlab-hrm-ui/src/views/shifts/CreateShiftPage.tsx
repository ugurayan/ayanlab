import React, { useMemo, useState } from "react";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigate } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";
import { Select } from "../../ui/components/Select";
import { useCreateShift, useLocations } from "../../data/hooks";
import { RequirePermission } from "../../router/guards";
import { useAuth } from "../../state/auth";
import { useTranslations } from "../../state/i18n";
import { TEXTS } from "./CreateShiftPage.i18n";
import { P } from "../../security/permissions";

const schema = z.object({
  locationId: z.string().min(1, "selectLocation"),
  role: z.string().min(2, "roleRequired"),
  startAt: z.string().min(1),
  endAt: z.string().min(1),
  headcount: z.coerce.number().int().min(1).max(500)
}).refine((v) => new Date(v.endAt).getTime() > new Date(v.startAt).getTime(), {
  message: "endAfterStart",
  path: ["endAt"]
});

type Form = z.infer<typeof schema>;

export function CreateShiftPage() {
  return (
    <RequirePermission anyOf={[P.shiftsCreate]}>
      <CreateShiftInner />
    </RequirePermission>
  );
}

function toLocalInput(d: Date) {
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

function CreateShiftInner() {
  const nav = useNavigate();
  const { user } = useAuth();
  const locations = useLocations();
  const create = useCreateShift();
  const [err, setErr] = useState<string | null>(null);
  const copy = useTranslations(TEXTS);

  const defaults = useMemo(() => {
    const start = new Date(Date.now() + 1000 * 60 * 60 * 24);
    start.setMinutes(0);
    const end = new Date(start.getTime() + 1000 * 60 * 60 * 8);
    return {
      locationId: user?.scope.locationIds?.[0] ?? "",
      role: "Retail Associate",
      startAt: toLocalInput(start),
      endAt: toLocalInput(end),
      headcount: 1
    };
  }, [user]);

  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<Form>({
    resolver: zodResolver(schema),
    defaultValues: defaults
  });

  async function onSubmit(values: Form) {
    setErr(null);
    try {
      await create.mutateAsync({
        orgId: user?.scope.orgId ?? "org_unknown",
        locationId: values.locationId,
        role: values.role,
        startAt: new Date(values.startAt).toISOString(),
        endAt: new Date(values.endAt).toISOString(),
        headcount: values.headcount,
        status: "PUBLISHED"
      });
      nav("/app/shifts");
    } catch (e: any) {
      setErr(e?.message ?? copy.errors.creation);
    }
  }

  function resolveError(message?: string | { message: string }) {
    if (!message) return "";
    const key = typeof message === "string" ? message : message.message;
    return copy.errors[key as keyof typeof copy.errors] ?? copy.errors.default ?? key;
  }

  return (
    <div className="space-y-6">
      <div>
        <div className="text-2xl font-bold">{copy.hero.title}</div>
        <div className="text-slate-500">{copy.hero.subtitle}</div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>{copy.form.title}</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="grid md:grid-cols-2 gap-4">
            <div className="md:col-span-2">
              <label className="text-sm font-semibold text-slate-700">{copy.form.location}</label>
              <Select {...register("locationId")}>
                <option value="">{copy.form.select}</option>
                {(locations.data ?? []).map((l: any) => (
                  <option key={l.id} value={l.id}>{l.name}</option>
                ))}
              </Select>
              {errors.locationId && <div className="text-xs font-semibold text-rose-700 mt-1">{resolveError(errors.locationId.message)}</div>}
            </div>

            <div>
              <label className="text-sm font-semibold text-slate-700">{copy.form.role}</label>
              <Input {...register("role")} />
              {errors.role && <div className="text-xs font-semibold text-rose-700 mt-1">{resolveError(errors.role.message)}</div>}
            </div>

            <div>
              <label className="text-sm font-semibold text-slate-700">{copy.form.headcount}</label>
              <Input type="number" min={1} max={500} {...register("headcount")} />
              {errors.headcount && <div className="text-xs font-semibold text-rose-700 mt-1">{resolveError(errors.headcount.message)}</div>}
            </div>

            <div>
              <label className="text-sm font-semibold text-slate-700">{copy.form.start}</label>
              <Input type="datetime-local" {...register("startAt")} />
              {errors.startAt && <div className="text-xs font-semibold text-rose-700 mt-1">{resolveError(errors.startAt.message)}</div>}
            </div>

            <div>
              <label className="text-sm font-semibold text-slate-700">{copy.form.end}</label>
              <Input type="datetime-local" {...register("endAt")} />
              {errors.endAt && <div className="text-xs font-semibold text-rose-700 mt-1">{resolveError(errors.endAt.message)}</div>}
            </div>

            {err && <div className="md:col-span-2 text-sm font-semibold text-rose-700 bg-rose-50 border border-rose-100 rounded-2xl p-3">{err}</div>}

            <div className="md:col-span-2 flex gap-2 justify-end">
              <Button type="button" variant="secondary" onClick={() => nav(-1)}>{copy.form.cancel}</Button>
              <Button type="submit" disabled={isSubmitting || create.isPending}>
                {isSubmitting || create.isPending ? copy.form.publishing : copy.form.publish}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
