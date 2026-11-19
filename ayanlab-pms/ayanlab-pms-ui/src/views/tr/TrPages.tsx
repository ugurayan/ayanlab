import { useEffect, useState, type ReactNode } from "react";
import {
  computeLatePenaltyPreview,
  useAtsCodes,
  useEnforcementCases,
  useLatePenaltyRule,
  useMeterAllocation,
  useMeterReadings,
  useRegenerateAts,
  useSaveLatePenaltyRule
} from "../../data/hooks";
import { formatDate, moneyTry } from "../../lib/format";
import { PageHeader } from "../../ui/components/PageHeader";
import { Badge } from "../../ui/components/Badge";
import { Button } from "../../ui/components/Button";
import { Card, CardContent } from "../../ui/components/Card";
import { Input } from "../../ui/components/Input";
import { Label } from "../../ui/components/Label";
import type { MeterReading } from "../../types/tr";

export function LatePenaltyPage() {
  const { data: rule } = useLatePenaltyRule();
  const save = useSaveLatePenaltyRule();
  const [rate, setRate] = useState(5);
  const [grace, setGrace] = useState(5);
  const [compound, setCompound] = useState(false);
  const [principal, setPrincipal] = useState(420000);
  const [daysLate, setDaysLate] = useState(45);

  useEffect(() => {
    if (rule) {
      setRate(rule.ratePercentMonthly);
      setGrace(rule.graceDays);
      setCompound(rule.compound);
    }
  }, [rule]);

  const previewRule = { ratePercentMonthly: rate, graceDays: grace, compound };
  const preview = computeLatePenaltyPreview(previewRule, principal, daysLate);

  return (
    <Shell title="Gecikme tazminatı" subtitle="Apsiyon — aidat gecikmesi kuralları">
      <div className="grid gap-4 md:grid-cols-2">
        <LatePenaltyForm
          rate={rate}
          setRate={setRate}
          grace={grace}
          setGrace={setGrace}
          compound={compound}
          setCompound={setCompound}
          onSave={() => save.mutate(previewRule)}
          saving={save.isPending}
        />
        <LatePenaltyPreview
          principal={principal}
          setPrincipal={setPrincipal}
          daysLate={daysLate}
          setDaysLate={setDaysLate}
          preview={preview}
        />
      </div>
    </Shell>
  );
}

function LatePenaltyForm({
  rate,
  setRate,
  grace,
  setGrace,
  compound,
  setCompound,
  onSave,
  saving
}: {
  rate: number;
  setRate: (v: number) => void;
  grace: number;
  setGrace: (v: number) => void;
  compound: boolean;
  setCompound: (v: boolean) => void;
  onSave: () => void;
  saving?: boolean;
}) {
  return (
    <div className="space-y-3 text-sm">
      <Field label="Aylık oran %">
        <Input type="number" value={rate} onChange={(e) => setRate(Number(e.target.value))} />
      </Field>
      <Field label="İntifa (gün)">
        <Input type="number" value={grace} onChange={(e) => setGrace(Number(e.target.value))} />
      </Field>
      <label className="flex items-center gap-2">
        <input type="checkbox" checked={compound} onChange={(e) => setCompound(e.target.checked)} />
        Bileşik faiz
      </label>
      <Button type="button" size="sm" onClick={onSave} isLoading={saving}>
        Kuralı kaydet
      </Button>
    </div>
  );
}

function LatePenaltyPreview({
  principal,
  setPrincipal,
  daysLate,
  setDaysLate,
  preview
}: {
  principal: number;
  setPrincipal: (v: number) => void;
  daysLate: number;
  setDaysLate: (v: number) => void;
  preview: ReturnType<typeof computeLatePenaltyPreview>;
}) {
  return (
    <div className="space-y-3 text-sm">
      <Field label="Anapara (kuruş)">
        <Input type="number" value={principal} onChange={(e) => setPrincipal(Number(e.target.value))} />
      </Field>
      <Field label="Gecikme (gün)">
        <Input type="number" value={daysLate} onChange={(e) => setDaysLate(Number(e.target.value))} />
      </Field>
      <table className="w-full text-xs">
        <thead>
          <tr className="text-[rgb(var(--text-muted))]">
            <th className="text-left">Gün</th>
            <th className="text-right">Tazminat</th>
          </tr>
        </thead>
        <tbody>
          {preview.map((p) => (
            <tr key={p.daysLate} className="border-t">
              <td className="py-1">{p.daysLate}</td>
              <td className="py-1 text-right">{moneyTry(p.penaltyCents)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export function MetersPage() {
  const { data } = useMeterReadings();
  const [readings, setReadings] = useState<MeterReading[]>([]);
  useEffect(() => {
    if (data) setReadings(data);
  }, [data]);
  const alloc = useMeterAllocation(readings);

  return (
    <Shell title="Sayaç okuma" subtitle="Tüketim dağıtım önizlemesi">
      <table className="mb-3 w-full text-sm">
        <thead>
          <tr className="text-xs text-[rgb(var(--text-muted))]">
            <th className="text-left">Daire</th>
            <th className="text-left">Tip</th>
            <th className="text-right">Önceki</th>
            <th className="text-right">Şimdiki</th>
          </tr>
        </thead>
        <tbody>
          {readings.map((r, i) => (
            <tr key={r.id} className="border-t">
              <td className="py-2">{r.unitLabel}</td>
              <td className="py-2">{r.meterType}</td>
              <td className="py-2 text-right">
                <Input
                  className="w-20 text-right"
                  type="number"
                  value={r.previous}
                  onChange={(e) => {
                    const next = [...readings];
                    next[i] = { ...r, previous: Number(e.target.value) };
                    setReadings(next);
                  }}
                />
              </td>
              <td className="py-2 text-right">
                <Input
                  className="w-20 text-right"
                  type="number"
                  value={r.current}
                  onChange={(e) => {
                    const next = [...readings];
                    next[i] = { ...r, current: Number(e.target.value) };
                    setReadings(next);
                  }}
                />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <h3 className="mb-2 text-sm font-semibold">Dağıtım</h3>
      <ul className="text-sm">
        {(alloc.data ?? []).map((a) => (
          <li key={a.unitLabel} className="flex justify-between border-t py-1">
            <span>
              {a.unitLabel} ({a.consumption} birim)
            </span>
            <span>{moneyTry(a.shareCents)}</span>
          </li>
        ))}
      </ul>
    </Shell>
  );
}

export function EnforcementPage() {
  const { data } = useEnforcementCases();
  return (
    <Shell title="İcra takibi" subtitle="TR — dosya durumu">
      <ul className="space-y-4 text-sm">
        {(data ?? []).map((c) => (
          <li key={c.id} className="rounded-xl border border-[rgb(var(--border))] p-3">
            <div className="flex justify-between">
              <div>
                <div className="font-medium">
                  {c.ownerName} · {c.unitLabel}
                </div>
                <div className="text-xs text-[rgb(var(--text-muted))]">Açılış {formatDate(c.openedAt)}</div>
              </div>
              <div className="text-right">
                <Badge tone={c.status === "CLOSED" ? "ok" : c.status === "LAWYER" ? "danger" : "warn"}>{c.status}</Badge>
                <div className="mt-1 font-semibold">{moneyTry(c.amountCents)}</div>
              </div>
            </div>
            <ul className="mt-2 border-t pt-2 text-xs text-[rgb(var(--text-muted))]">
              {c.timeline.map((t, i) => (
                <li key={i}>
                  {formatDate(t.at)} — {t.note}
                </li>
              ))}
            </ul>
          </li>
        ))}
      </ul>
    </Shell>
  );
}

export function AtsPage() {
  const { data } = useAtsCodes();
  const regen = useRegenerateAts();
  return (
    <Shell title="ATS borç kodu" subtitle="10 haneli sorgu kodları">
      <table className="w-full text-sm">
        <thead>
          <tr className="text-xs text-[rgb(var(--text-muted))]">
            <th className="text-left">Kişi</th>
            <th className="text-left">Daire</th>
            <th className="text-left">Kod</th>
            <th />
          </tr>
        </thead>
        <tbody>
          {(data ?? []).map((a) => (
            <tr key={a.id} className="border-t">
              <td className="py-2">{a.partyName}</td>
              <td className="py-2">{a.unitLabel}</td>
              <td className="py-2 font-mono text-xs">{a.code10}</td>
              <td className="py-2 text-right">
                <Button type="button" size="sm" variant="secondary" onClick={() => regen.mutate(a.id)} isLoading={regen.isPending}>
                  Yenile
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </Shell>
  );
}

function Shell({ title, subtitle, children }: { title: string; subtitle?: string; children: ReactNode }) {
  return (
    <div>
      <PageHeader title={title} subtitle={subtitle} />
      <Card>
        <CardContent>{children}</CardContent>
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