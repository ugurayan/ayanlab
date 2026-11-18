import React from "react";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";

export type Certificate = {
  id: string;
  name: string;
};

export type Document = {
  id: string;
  name: string;
};

export type CertificatesDocumentsModalData = {
  selectedCertificates: string[];
  selectedDocuments: string[];
};

interface CertificatesDocumentsModalContentProps {
  data: CertificatesDocumentsModalData;
  certificates: Certificate[] | undefined;
  documents: Document[] | undefined;
  onDataChange: (data: CertificatesDocumentsModalData) => void;
  onClose: () => void;
  isLoading?: boolean;
}

export function CertificatesDocumentsModalContent({
  data,
  certificates,
  documents,
  onDataChange,
  onClose,
  isLoading = false,
}: CertificatesDocumentsModalContentProps) {
  const handleCertificateToggle = (id: string, checked: boolean) => {
    onDataChange({
      ...data,
      selectedCertificates: checked
        ? [...data.selectedCertificates, id]
        : data.selectedCertificates.filter(existingId => existingId !== id),
    });
  };

  const handleDocumentToggle = (id: string, checked: boolean) => {
    onDataChange({
      ...data,
      selectedDocuments: checked
        ? [...data.selectedDocuments, id]
        : data.selectedDocuments.filter(existingId => existingId !== id),
    });
  };

  return (
    <div className="space-y-4">
      {/* Certificates Section */}
      <div className="space-y-3">
        <div className="text-sm font-semibold text-slate-900">Required Certificates</div>
        {data.selectedCertificates.length > 0 && (
          <div>
            <div className="text-xs text-slate-500 mb-1">Selected certificates:</div>
            <Input
              value={certificates
                ?.filter(cert => data.selectedCertificates.includes(cert.id))
                .map(cert => cert.name)
                .join(", ") || ""}
              readOnly
              className="bg-slate-100 text-slate-600 cursor-not-allowed"
            />
          </div>
        )}
        {isLoading ? (
          <div className="text-sm text-slate-500 py-4">Loading certificates...</div>
        ) : certificates && certificates.length > 0 ? (
          <div className="space-y-2 max-h-60 overflow-y-auto border border-slate-200 rounded-lg p-3">
            {certificates.map((cert) => (
              <label
                key={cert.id}
                className={`
                  flex items-center gap-2 p-2 rounded cursor-pointer transition-colors
                  ${data.selectedCertificates.includes(cert.id)
                    ? 'bg-blue-50'
                    : 'hover:bg-slate-50'
                  }
                `}
              >
                <input
                  type="checkbox"
                  checked={data.selectedCertificates.includes(cert.id)}
                  onChange={(e) => handleCertificateToggle(cert.id, e.target.checked)}
                  className="rounded border-slate-300"
                />
                <span className="text-sm text-slate-700">{cert.name}</span>
              </label>
            ))}
          </div>
        ) : (
          <div className="text-sm text-slate-500 py-4">
            No certificates available. Please select a job role first.
          </div>
        )}
      </div>

      {/* Documents Section */}
      <div className="space-y-3">
        <div className="text-sm font-semibold text-slate-900">Required Documents</div>
        {data.selectedDocuments.length > 0 && (
          <div>
            <div className="text-xs text-slate-500 mb-1">Selected documents:</div>
            <Input
              value={documents
                ?.filter(doc => data.selectedDocuments.includes(doc.id))
                .map(doc => doc.name || doc.title || doc.id)
                .join(", ") || ""}
              readOnly
              className="bg-slate-100 text-slate-600 cursor-not-allowed"
            />
          </div>
        )}
        {isLoading ? (
          <div className="text-sm text-slate-500 py-4">Loading documents...</div>
        ) : documents && documents.length > 0 ? (
          <div className="space-y-2 max-h-60 overflow-y-auto border border-slate-200 rounded-lg p-3">
            {documents.map((doc) => (
              <label
                key={doc.id}
                className={`
                  flex items-center gap-2 p-2 rounded cursor-pointer transition-colors
                  ${data.selectedDocuments.includes(doc.id)
                    ? 'bg-blue-50'
                    : 'hover:bg-slate-50'
                  }
                `}
              >
                <input
                  type="checkbox"
                  checked={data.selectedDocuments.includes(doc.id)}
                  onChange={(e) => handleDocumentToggle(doc.id, e.target.checked)}
                  className="rounded border-slate-300"
                />
                <span className="text-sm text-slate-700">{doc.name || doc.title || doc.id}</span>
              </label>
            ))}
          </div>
        ) : (
          <div className="text-sm text-slate-500 py-4">
            No documents available. Please select a job role first.
          </div>
        )}
      </div>

      <div className="flex justify-end gap-2 pt-4 border-t">
        <Button type="button" variant="secondary" onClick={onClose}>
          Back
        </Button>
        <Button type="button" onClick={onClose}>
          Next
        </Button>
      </div>
    </div>
  );
}

