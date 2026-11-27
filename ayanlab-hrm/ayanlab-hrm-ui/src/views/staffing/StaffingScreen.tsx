import React, { useState } from "react";
import { Button } from "../../ui/components/Button";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Input } from "../../ui/components/Input";
import { Table, Td, Th } from "../../ui/components/Table";
import { useAuth } from "../../state/auth";
import { apiErrorMessage } from "../../api/services/staffing";

export type Field = { key: string; label: string; type?: string; readOnly?: boolean };

function displayValue<T>(draft: Partial<T>, field: Field) {
  const raw = (draft as Record<string, unknown>)[field.key];
  if (field.type === "datetime-local" && typeof raw === "string" && raw.includes("T")) {
    const date = new Date(raw);
    if (!Number.isNaN(date.getTime())) {
      const pad = (n: number) => String(n).padStart(2, "0");
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
    }
  }
  return raw == null ? "" : String(raw);
}

type Query<T> = {
  data?: T[];
  isLoading: boolean;
  error: unknown;
  save: { mutateAsync: (input: { body: Partial<T>; id?: string }) => Promise<unknown>; isPending: boolean };
  remove: { mutateAsync: (id: string) => Promise<unknown> };
};

export function StaffingScreen<T extends { id: string }>(props: {
  title: string;
  subtitle: string;
  empty: string;
  read: string;
  create: string;
  update: string;
  remove: string;
  columns: { key: keyof T; label: string }[];
  fields: Field[];
  blank: () => Partial<T>;
  query: Query<T>;
  formExtra?: (draft: Partial<T>, setDraft: (next: Partial<T>) => void) => React.ReactNode;
}) {
  const { has } = useAuth();
  const [draft, setDraft] = useState<Partial<T> | null>(null);
  const [formError, setFormError] = useState<string | null>(null);
  const rows = props.query.data ?? [];

  async function onSave(event: React.FormEvent) {
    event.preventDefault();
    if (!draft) return;
    setFormError(null);
    try {
      await props.query.save.mutateAsync({ body: draft, id: draft.id });
      setDraft(null);
    } catch (error) {
      setFormError(apiErrorMessage(error));
    }
  }

  async function onDelete(row: T) {
    if (!window.confirm("This record will be deactivated.")) return;
    try {
      await props.query.remove.mutateAsync(row.id);
    } catch (error) {
      window.alert(apiErrorMessage(error));
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-end justify-between gap-4">
        <div>
          <div className="page-title text-2xl">{props.title}</div>
          <div className="page-subtitle">{props.subtitle}</div>
        </div>
        {has(props.create) && (
          <Button onClick={() => { setFormError(null); setDraft(props.blank()); }}>Create</Button>
        )}
      </div>
      <Card>
        <CardHeader><CardTitle>{props.title}</CardTitle></CardHeader>
        <CardContent>
          {props.query.isLoading && <div className="text-slate-500">Loading…</div>}
          {props.query.error != null && <div className="text-rose-700">{apiErrorMessage(props.query.error)}</div>}
          {!props.query.isLoading && rows.length === 0 && <div className="text-slate-500 text-sm">{props.empty}</div>}
          {rows.length > 0 && (
            <Table>
              <thead>
                <tr>
                  {props.columns.map((column) => <Th key={String(column.key)}>{column.label}</Th>)}
                  <Th></Th>
                </tr>
              </thead>
              <tbody>
                {rows.map((row) => (
                  <tr key={row.id}>
                    {props.columns.map((column) => <Td key={String(column.key)}>{String(row[column.key] ?? "")}</Td>)}
                    <Td>
                      <div className="flex gap-2 justify-end">
                        {has(props.update) && <Button variant="secondary" onClick={() => { setFormError(null); setDraft(row); }}>Edit</Button>}
                        {has(props.remove) && <Button variant="secondary" onClick={() => onDelete(row)}>Delete</Button>}
                      </div>
                    </Td>
                  </tr>
                ))}
              </tbody>
            </Table>
          )}
        </CardContent>
      </Card>
      {draft && (
        <div className="fixed inset-0 z-40 bg-slate-900/40 flex items-center justify-center p-4">
          <form onSubmit={onSave} className="bg-white rounded-3xl p-6 w-full max-w-xl space-y-3 max-h-[90vh] overflow-auto">
            <div className="text-lg font-semibold">{draft.id ? "Edit" : "Create"}</div>
            {props.fields.map((field) => (
              <label key={field.key} className="block text-sm">
                <span className="font-semibold text-slate-700">{field.label}</span>
                <Input
                  className="mt-1"
                  type={field.type ?? "text"}
                  readOnly={field.readOnly}
                  value={displayValue(draft, field)}
                  onChange={(event) => setDraft({ ...draft, [field.key]: event.target.value })}
                />
              </label>
            ))}
            {props.formExtra?.(draft, setDraft)}
            {formError && (
              <div className="rounded-xl border border-rose-100 bg-rose-50 p-3 text-sm font-semibold text-rose-700">
                {formError}
              </div>
            )}
            <div className="flex justify-end gap-2">
              <Button type="button" variant="secondary" onClick={() => setDraft(null)}>Cancel</Button>
              <Button type="submit" disabled={props.query.save.isPending}>Save</Button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}
