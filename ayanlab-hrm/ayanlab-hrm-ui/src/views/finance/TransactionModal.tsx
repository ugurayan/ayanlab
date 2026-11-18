import React, { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";
import { useTranslations } from "../../state/i18n";
import { useAuth } from "../../state/auth";
import { useWorkers } from "../../data/hooks";
import type { Transaction, PaymentType, TransactionStatus } from "../../types/transactions";
import { X } from "lucide-react";
import { TEXTS } from "./i18n";

type TransactionModalProps = {
  transaction: Transaction | null;
  onClose: () => void;
};

export function TransactionModal({ transaction, onClose }: TransactionModalProps) {
  const { user } = useAuth();
  const workersQuery = useWorkers();
  const copy = useTranslations(TEXTS);
  const isEdit = !!transaction;

  const [formData, setFormData] = useState({
    workerId: transaction?.workerId || "",
    orgId: transaction?.orgId || "",
    amountCents: transaction ? String(transaction.amountCents / 100) : "",
    currency: transaction?.currency || "CHF",
    direction: transaction?.direction || ("credit" as "credit" | "debit"),
    status: transaction?.status || ("pending" as TransactionStatus),
    description: transaction?.description || "",
    jobId: transaction?.jobId || "",
    jobTitle: transaction?.jobTitle || "",
    shiftId: transaction?.shiftId || "",
    paymentType: transaction?.paymentType || ("bank_transfer" as PaymentType),
    bankName: transaction?.bankName || "",
    bankAccountLast4: transaction?.bankAccountLast4 || "",
    creditCardLast4: transaction?.creditCardLast4 || "",
    creditCardBrand: transaction?.creditCardBrand || "",
    feesCents: transaction ? String(transaction.feesCents / 100) : "",
    externalReferenceId: transaction?.externalReferenceId || "",
    relatedInvoiceId: transaction?.relatedInvoiceId || ""
  });

  const workers = (workersQuery.data ?? []) as any[];

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // In real app: POST/PUT to backend
    console.log("Save transaction:", {
      ...formData,
      amountCents: Math.round(parseFloat(formData.amountCents) * 100),
      feesCents: Math.round(parseFloat(formData.feesCents || "0") * 100),
      createdBy: isEdit ? transaction?.createdBy : user?.id,
      createdByName: isEdit ? transaction?.createdByName : user?.displayName,
      updatedBy: user?.id,
      updatedByName: user?.displayName,
      updatedAt: new Date().toISOString()
    });
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 p-4">
      <Card className="w-full max-w-3xl max-h-[90vh] overflow-y-auto">
        <CardHeader className="flex items-center justify-between">
          <CardTitle>{isEdit ? copy.modal.editTitle : copy.modal.createTitle}</CardTitle>
          <Button variant="ghost" onClick={onClose} className="px-2 py-1">
            <X size={16} />
          </Button>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid gap-4 md:grid-cols-2">
              <div>
                <label className="text-xs font-semibold text-slate-600 mb-1 block">
                  {copy.modal.direction}
                </label>
                <select
                  className="w-full rounded-2xl border border-slate-200 px-3 py-2 text-sm bg-white"
                  value={formData.direction}
                  onChange={(e) =>
                    setFormData((prev) => ({
                      ...prev,
                      direction: e.target.value as "credit" | "debit"
                    }))
                  }
                >
                  <option value="credit">Credit (Payout to worker)</option>
                  <option value="debit">Debit (Payment from company)</option>
                </select>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-600 mb-1 block">
                  {copy.modal.status}
                </label>
                <select
                  className="w-full rounded-2xl border border-slate-200 px-3 py-2 text-sm bg-white"
                  value={formData.status}
                  onChange={(e) =>
                    setFormData((prev) => ({
                      ...prev,
                      status: e.target.value as TransactionStatus
                    }))
                  }
                >
                  <option value="pending">Pending</option>
                  <option value="completed">Completed</option>
                  <option value="failed">Failed</option>
                  <option value="refunded">Refunded</option>
                </select>
              </div>

              {formData.direction === "credit" && (
                <div>
                  <label className="text-xs font-semibold text-slate-600 mb-1 block">
                    {copy.modal.worker}
                  </label>
                  <select
                    className="w-full rounded-2xl border border-slate-200 px-3 py-2 text-sm bg-white"
                    value={formData.workerId}
                    onChange={(e) =>
                      setFormData((prev) => ({ ...prev, workerId: e.target.value }))
                    }
                  >
                    <option value="">Select worker</option>
                    {workers.map((w) => (
                      <option key={w.id} value={w.id}>
                        {w.displayName}
                      </option>
                    ))}
                  </select>
                </div>
              )}

              <div>
                <label className="text-xs font-semibold text-slate-600 mb-1 block">
                  {copy.modal.orgId}
                </label>
                <Input
                  value={formData.orgId}
                  onChange={(e) =>
                    setFormData((prev) => ({ ...prev, orgId: e.target.value }))
                  }
                  placeholder="org_acme"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-600 mb-1 block">
                  {copy.modal.amount}
                </label>
                <div className="flex gap-2">
                  <Input
                    type="number"
                    step="0.01"
                    value={formData.amountCents}
                    onChange={(e) =>
                      setFormData((prev) => ({ ...prev, amountCents: e.target.value }))
                    }
                    placeholder="1000.00"
                    className="flex-1"
                  />
                  <select
                    className="rounded-2xl border border-slate-200 px-3 py-2 text-sm bg-white w-24"
                    value={formData.currency}
                    onChange={(e) =>
                      setFormData((prev) => ({ ...prev, currency: e.target.value }))
                    }
                  >
                    <option value="CHF">CHF</option>
                    <option value="EUR">EUR</option>
                    <option value="USD">USD</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-600 mb-1 block">
                  {copy.modal.fees}
                </label>
                <Input
                  type="number"
                  step="0.01"
                  value={formData.feesCents}
                  onChange={(e) =>
                    setFormData((prev) => ({ ...prev, feesCents: e.target.value }))
                  }
                  placeholder="20.00"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-600 mb-1 block">
                  {copy.modal.jobTitle}
                </label>
                <Input
                  value={formData.jobTitle}
                  onChange={(e) =>
                    setFormData((prev) => ({ ...prev, jobTitle: e.target.value }))
                  }
                  placeholder="Retail Associate"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-600 mb-1 block">
                  {copy.modal.shiftId}
                </label>
                <Input
                  value={formData.shiftId}
                  onChange={(e) =>
                    setFormData((prev) => ({ ...prev, shiftId: e.target.value }))
                  }
                  placeholder="sh_1"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-600 mb-1 block">
                  {copy.modal.paymentType}
                </label>
                <select
                  className="w-full rounded-2xl border border-slate-200 px-3 py-2 text-sm bg-white"
                  value={formData.paymentType}
                  onChange={(e) =>
                    setFormData((prev) => ({
                      ...prev,
                      paymentType: e.target.value as PaymentType
                    }))
                  }
                >
                  <option value="bank_transfer">Bank Transfer</option>
                  <option value="credit_card">Credit Card</option>
                  <option value="debit_card">Debit Card</option>
                  <option value="paypal">PayPal</option>
                  <option value="other">Other</option>
                </select>
              </div>

              {formData.paymentType === "bank_transfer" && (
                <>
                  <div>
                    <label className="text-xs font-semibold text-slate-600 mb-1 block">
                      {copy.modal.bankName}
                    </label>
                    <Input
                      value={formData.bankName}
                      onChange={(e) =>
                        setFormData((prev) => ({ ...prev, bankName: e.target.value }))
                      }
                      placeholder="UBS"
                    />
                  </div>
                  <div>
                    <label className="text-xs font-semibold text-slate-600 mb-1 block">
                      {copy.modal.bankAccountLast4}
                    </label>
                    <Input
                      value={formData.bankAccountLast4}
                      onChange={(e) =>
                        setFormData((prev) => ({ ...prev, bankAccountLast4: e.target.value }))
                      }
                      placeholder="1234"
                      maxLength={4}
                    />
                  </div>
                </>
              )}

              {(formData.paymentType === "credit_card" ||
                formData.paymentType === "debit_card") && (
                <>
                  <div>
                    <label className="text-xs font-semibold text-slate-600 mb-1 block">
                      {copy.modal.cardBrand}
                    </label>
                    <select
                      className="w-full rounded-2xl border border-slate-200 px-3 py-2 text-sm bg-white"
                      value={formData.creditCardBrand}
                      onChange={(e) =>
                        setFormData((prev) => ({ ...prev, creditCardBrand: e.target.value }))
                      }
                    >
                      <option value="">Select brand</option>
                      <option value="Visa">Visa</option>
                      <option value="Mastercard">Mastercard</option>
                      <option value="American Express">American Express</option>
                    </select>
                  </div>
                  <div>
                    <label className="text-xs font-semibold text-slate-600 mb-1 block">
                      {copy.modal.cardLast4}
                    </label>
                    <Input
                      value={formData.creditCardLast4}
                      onChange={(e) =>
                        setFormData((prev) => ({ ...prev, creditCardLast4: e.target.value }))
                      }
                      placeholder="4242"
                      maxLength={4}
                    />
                  </div>
                </>
              )}

              <div className="md:col-span-2">
                <label className="text-xs font-semibold text-slate-600 mb-1 block">
                  {copy.modal.description}
                </label>
                <textarea
                  className="w-full rounded-2xl border border-slate-200 px-3 py-2 text-sm min-h-[80px]"
                  value={formData.description}
                  onChange={(e) =>
                    setFormData((prev) => ({ ...prev, description: e.target.value }))
                  }
                  placeholder="Payout for Retail Associate shift"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-600 mb-1 block">
                  {copy.modal.externalReference}
                </label>
                <Input
                  value={formData.externalReferenceId}
                  onChange={(e) =>
                    setFormData((prev) => ({ ...prev, externalReferenceId: e.target.value }))
                  }
                  placeholder="EXT-ABC123"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-600 mb-1 block">
                  {copy.modal.invoiceId}
                </label>
                <Input
                  value={formData.relatedInvoiceId}
                  onChange={(e) =>
                    setFormData((prev) => ({ ...prev, relatedInvoiceId: e.target.value }))
                  }
                  placeholder="inv_1"
                />
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-4 border-t">
              <Button variant="secondary" type="button" onClick={onClose}>
                {copy.modal.cancel}
              </Button>
              <Button type="submit">{copy.modal.save}</Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}

