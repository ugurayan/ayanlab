import React, { useCallback, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Upload as UploadIcon } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Button } from "../../ui/components/Button";
import { useDocumentTypes } from "../../data/hooks/useDocumentTypes";
import { useJobRoleCategories } from "../../data/hooks/useJobRoleCategories";
import { useTranslations } from "../../state/i18n";
import { TEXTS } from "./i18n";
import { cn } from "../../lib/cn";

export function UploadFilePage() {
  const navigate = useNavigate();
  const copy = useTranslations(TEXTS);
  const [isDragging, setIsDragging] = useState(false);
  const [files, setFiles] = useState<File[]>([]);
  const [documentTypeId, setDocumentTypeId] = useState<string>("");
  const [categoryId, setCategoryId] = useState<string>("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const { data: typesData, isLoading: typesLoading } = useDocumentTypes(0, 500);
  const { data: categoriesData, isLoading: categoriesLoading } = useJobRoleCategories(0, 500);

  const documentTypes = typesData?.items ?? [];
  const categories = categoriesData?.items ?? [];

  const onDropFiles = useCallback((dropped: FileList | null) => {
    if (!dropped || dropped.length === 0) return;
    setFiles(Array.from(dropped));
    setError(null);
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

  const removeFile = (index: number) => {
    setFiles((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!documentTypeId.trim()) {
      setError(copy.validationType);
      return;
    }
    if (!categoryId.trim()) {
      setError(copy.validationCategory);
      return;
    }
    if (files.length === 0) {
      setError(copy.validationFiles);
      return;
    }

    setIsSubmitting(true);
    try {
      // TODO: Replace with real API when endpoint is available.
      // Example: const formData = new FormData(); formData.append('documentTypeId', documentTypeId);
      // formData.append('categoryId', categoryId); files.forEach(f => formData.append('files', f));
      // await authHttp.post(endpoints.documents.upload, formData, { headers: { 'Content-Type': 'multipart/form-data' } });
      await new Promise((resolve) => setTimeout(resolve, 800));
      console.log("Upload (mock)", {
        documentTypeId,
        categoryId,
        fileCount: files.length,
        fileNames: files.map((f) => f.name),
      });
      alert(copy.successMessage);
      setFiles([]);
      setDocumentTypeId("");
      setCategoryId("");
      navigate("/app/documents");
    } catch (err) {
      console.error("Upload failed", err);
      setError(copy.errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <div className="flex flex-col gap-1 mb-6">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold text-slate-900">{copy.title}</h1>
          <Button
            type="button"
            variant="secondary"
            onClick={() => navigate("/app/documents")}
          >
            {copy.backToDocuments}
          </Button>
        </div>
        <p className="text-sm text-slate-500">{copy.subtitle}</p>
      </div>

      <form onSubmit={handleSubmit}>
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <UploadIcon size={20} className="text-slate-600" />
              {copy.title}
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            {/* Type & Category */}
            <div className="grid gap-4 sm:grid-cols-2">
              <div className="space-y-1.5">
                <label className="block text-sm font-medium text-slate-700">
                  {copy.typeLabel}
                </label>
                <select
                  className={cn(
                    "w-full rounded-xl border bg-white px-3 py-2.5 text-sm shadow-sm",
                    "border-slate-200 text-slate-900 focus:border-slate-400 focus:outline-none focus:ring-1 focus:ring-slate-400"
                  )}
                  value={documentTypeId}
                  onChange={(e) => setDocumentTypeId(e.target.value)}
                  aria-label={copy.typeLabel}
                >
                  <option value="">{copy.typePlaceholder}</option>
                  {typesLoading ? (
                    <option disabled>{copy.loadingTypes}</option>
                  ) : (
                    documentTypes.map((dt) => (
                      <option key={dt.id} value={dt.id}>
                        {dt.name}
                      </option>
                    ))
                  )}
                </select>
              </div>
              <div className="space-y-1.5">
                <label className="block text-sm font-medium text-slate-700">
                  {copy.categoryLabel}
                </label>
                <select
                  className={cn(
                    "w-full rounded-xl border bg-white px-3 py-2.5 text-sm shadow-sm",
                    "border-slate-200 text-slate-900 focus:border-slate-400 focus:outline-none focus:ring-1 focus:ring-slate-400"
                  )}
                  value={categoryId}
                  onChange={(e) => setCategoryId(e.target.value)}
                  aria-label={copy.categoryLabel}
                >
                  <option value="">{copy.categoryPlaceholder}</option>
                  {categoriesLoading ? (
                    <option disabled>{copy.loadingCategories}</option>
                  ) : (
                    categories.map((cat) => (
                      <option key={cat.id} value={cat.id}>
                        {cat.name}
                      </option>
                    ))
                  )}
                </select>
              </div>
            </div>

            {/* File drop zone */}
            <div className="space-y-1.5">
              <span className="block text-sm font-medium text-slate-700">
                {copy.selectedFiles}
              </span>
              <div
                className={cn(
                  "flex flex-col items-center justify-center rounded-2xl border-2 border-dashed px-6 py-10 text-center transition cursor-pointer",
                  isDragging
                    ? "border-slate-700 bg-slate-100"
                    : "border-slate-300 bg-slate-50/80 hover:border-slate-400 hover:bg-slate-50"
                )}
                onDragOver={handleDragOver}
                onDragLeave={handleDragLeave}
                onDrop={handleDrop}
              >
                <input
                  type="file"
                  multiple
                  className="hidden"
                  id="upload-file-input"
                  onChange={handleFileInputChange}
                />
                <label htmlFor="upload-file-input" className="cursor-pointer">
                  <UploadIcon size={40} className="mx-auto text-slate-400 mb-2" />
                  <div className="text-sm font-semibold text-slate-700">
                    {copy.dropTitle}
                  </div>
                  <div className="text-xs text-slate-500 mt-1">{copy.dropSubtitle}</div>
                  <Button
                    type="button"
                    variant="secondary"
                    className="mt-3"
                    onClick={(e) => {
                      e.preventDefault();
                      document.getElementById("upload-file-input")?.click();
                    }}
                  >
                    {copy.browseButton}
                  </Button>
                </label>
                {files.length > 0 && (
                  <div className="mt-4 w-full max-w-md text-left">
                    <ul className="space-y-1.5">
                      {files.map((f, i) => (
                        <li
                          key={`${f.name}-${i}`}
                          className="flex items-center justify-between gap-2 rounded-lg bg-white px-3 py-2 text-sm text-slate-700 border border-slate-200"
                        >
                          <span className="truncate flex-1" title={f.name}>
                            {f.name}
                          </span>
                          <span className="text-xs text-slate-400 flex-shrink-0">
                            {Math.round(f.size / 1024)} KB
                          </span>
                          <button
                            type="button"
                            className="text-slate-400 hover:text-red-600 p-0.5"
                            onClick={(e) => {
                              e.preventDefault();
                              removeFile(i);
                            }}
                            aria-label={`Remove ${f.name}`}
                          >
                            ×
                          </button>
                        </li>
                      ))}
                    </ul>
                  </div>
                )}
              </div>
            </div>

            {error && (
              <p className="text-sm text-red-600 bg-red-50 px-3 py-2 rounded-lg">
                {error}
              </p>
            )}

            <div className="flex justify-end gap-2 pt-2">
              <Button
                type="button"
                variant="secondary"
                onClick={() => navigate("/app/documents")}
              >
                {copy.cancel}
              </Button>
              <Button type="submit" disabled={isSubmitting || files.length === 0}>
                {isSubmitting ? "Uploading…" : copy.submit}
              </Button>
            </div>
          </CardContent>
        </Card>
      </form>
    </div>
  );
}
