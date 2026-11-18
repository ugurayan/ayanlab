import React, { useCallback, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";
import { useTranslations } from "../../state/i18n";
import { TEXTS as DOCUMENT_TEXTS } from "./i18n";

type CategoryKey =
  | "identity"
  | "address"
  | "workEligibility"
  | "certificate"
  | "photo"
  | "background";

export function UploadDocumentPage() {
  const navigate = useNavigate();
  const copy = useTranslations(DOCUMENT_TEXTS);
  const [isDragging, setIsDragging] = useState(false);
  const [files, setFiles] = useState<File[]>([]);
  const [category, setCategory] = useState<CategoryKey>("identity");
  const [subtype, setSubtype] = useState<string>("");
  const [customName, setCustomName] = useState<string>("");

  const categoryOptions: { key: CategoryKey; label: string }[] = [
    { key: "identity", label: copy.upload.categories.identityVerification },
    { key: "address", label: copy.upload.categories.proofOfAddress },
    { key: "workEligibility", label: copy.upload.categories.workEligibility },
    { key: "certificate", label: copy.upload.categories.certificates },
    { key: "photo", label: copy.upload.categories.photo },
    { key: "background", label: copy.upload.categories.backgroundCheck }
  ];

  const subtypeOptions = copy.upload.subtypes[category];

  const onDropFiles = useCallback((dropped: FileList | null) => {
    if (!dropped || dropped.length === 0) return;
    setFiles(Array.from(dropped));
  }, []);

  const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(false);
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(false);
    onDropFiles(e.dataTransfer.files);
  };

  const handleFileInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onDropFiles(e.target.files);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // In real app: upload via API. For now, just log and go back.
    console.log("Mock upload", {
      category,
      subtype,
      customName,
      files
    });
    navigate("/app/documents");
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <div className="text-2xl font-bold">{copy.upload.heroTitle}</div>
          <div className="text-slate-500 text-sm">{copy.upload.heroSubtitle}</div>
        </div>
        <Button variant="secondary" type="button" onClick={() => navigate("/app/documents")}>
          {copy.upload.backToList}
        </Button>
      </div>

      <form onSubmit={handleSubmit}>
        <Card>
          <CardHeader>
            <CardTitle>{copy.upload.sectionTitle}</CardTitle>
          </CardHeader>
          <CardContent className="space-y-6 text-sm">
            {/* Drag & drop */}
            <div
              className={[
                "flex flex-col items-center justify-center rounded-2xl border-2 border-dashed px-6 py-10 text-center cursor-pointer transition",
                isDragging ? "border-slate-900 bg-slate-50" : "border-slate-300 bg-slate-50/50"
              ].join(" ")}
              onDragOver={handleDragOver}
              onDragLeave={handleDragLeave}
              onDrop={handleDrop}
            >
              <input
                type="file"
                multiple
                className="hidden"
                id="file-input"
                onChange={handleFileInputChange}
              />
              <label htmlFor="file-input" className="cursor-pointer">
                <div className="text-sm font-semibold text-slate-800">
                  {copy.upload.dropTitle}
                </div>
                <div className="text-xs text-slate-500 mt-1">
                  {copy.upload.dropSubtitle}
                </div>
                <Button
                  type="button"
                  variant="secondary"
                  className="mt-3 text-xs"
                  onClick={() => document.getElementById("file-input")?.click()}
                >
                  {copy.upload.browseButton}
                </Button>
              </label>
              {files.length > 0 && (
                <div className="mt-4 w-full max-w-md text-xs text-slate-600 text-left">
                  <div className="font-semibold mb-1">{copy.upload.selectedFiles}</div>
                  <ul className="list-disc pl-4 space-y-0.5">
                    {files.map((f) => (
                      <li key={f.name}>
                        {f.name}{" "}
                        <span className="text-slate-400">
                          ({Math.round(f.size / 1024)} KB)
                        </span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </div>

            {/* Category & subtype */}
            <div className="grid gap-4 md:grid-cols-2">
              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-600">
                  {copy.upload.categoryLabel}
                </label>
                <select
                  className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm"
                  value={category}
                  onChange={(e) => {
                    setCategory(e.target.value as CategoryKey);
                    setSubtype("");
                  }}
                >
                  {categoryOptions.map((opt) => (
                    <option key={opt.key} value={opt.key}>
                      {opt.label}
                    </option>
                  ))}
                </select>
              </div>
              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-600">
                  {copy.upload.subtypeLabel}
                </label>
                <select
                  className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm"
                  value={subtype}
                  onChange={(e) => setSubtype(e.target.value)}
                >
                  <option value="">{copy.upload.subtypePlaceholder}</option>
                  {subtypeOptions?.items.map((optKey: string) => (
                    <option key={optKey} value={optKey}>
                      {copy.upload.subtypesLabels[optKey as keyof typeof copy.upload.subtypesLabels]}
                    </option>
                  ))}
                  <option value="other">{copy.upload.subtypesLabels.other}</option>
                </select>
                {subtypeOptions?.hint && (
                  <p className="text-[11px] text-slate-500 mt-1">{subtypeOptions.hint}</p>
                )}
              </div>
            </div>

            {/* Custom other name */}
            <div>
              <label className="text-xs font-semibold text-slate-600">
                {copy.upload.customLabel}
              </label>
              <Input
                placeholder={copy.upload.customPlaceholder}
                className="mt-1"
                value={customName}
                onChange={(e) => setCustomName(e.target.value)}
              />
              <p className="text-[11px] text-slate-500 mt-1">
                {copy.upload.customHint}
              </p>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="secondary" onClick={() => navigate("/app/documents")}>
                {copy.upload.cancel}
              </Button>
              <Button type="submit" disabled={files.length === 0}>
                {copy.upload.submit}
              </Button>
            </div>
          </CardContent>
        </Card>
      </form>
    </div>
  );
}


