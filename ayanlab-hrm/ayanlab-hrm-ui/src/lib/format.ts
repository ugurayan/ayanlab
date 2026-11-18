export function formatMoney(amountCents: number, currency = "USD") {
  const n = amountCents / 100;
  return new Intl.NumberFormat(undefined, { style: "currency", currency }).format(n);
}

export function formatDateTime(iso: string) {
  const d = new Date(iso);
  return d.toLocaleString();
}

export function formatDate(iso: string) {
  const d = new Date(iso);
  return d.toLocaleDateString();
}
