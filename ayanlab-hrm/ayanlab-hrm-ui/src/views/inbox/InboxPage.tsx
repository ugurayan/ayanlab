import React, { useMemo, useState, useCallback } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";
import type { InboxFolder, InboxMessage, InboxAttachment } from "../../types/inbox";
import { cn } from "../../lib/cn";
import { useTranslations, useLanguage } from "../../state/i18n";
import { TEXTS } from "./i18n";
import { useDepartments, useDepartmentIssues, useJobs } from "../../data/hooks";
import { X, Paperclip, ChevronDown, ChevronUp, History } from "lucide-react";

// Example conversation thread
const mockMessages: InboxMessage[] = [
  // Original message in a thread
  {
    id: "thread1_original",
    fromName: "Myra Dudley",
    fromDepartment: "Trust and Safety",
    avatarUrl: "https://i.pravatar.cc/64?img=5",
    subject: "Please review and sign the attached agreement",
    preview: "Hi Brian, I need you to review the attached agreement...",
    body:
      "Hi Brian,\n\nI hope this message finds you well. I need you to review and sign the attached agreement regarding the new project terms. Please take a look at the document and let me know if you have any questions.\n\nThe agreement covers the payment terms, project timeline, and deliverables. I've highlighted the key sections that need your attention.\n\nPlease review it at your earliest convenience and get back to me.\n\nBest regards,\nMyra Dudley",
    dateLabel: "Jan 23",
    folder: "inbox",
    labels: ["personal", "work", "payments"],
    unread: false,
    timestamp: 1705968000000, // Jan 23, 2024
    attachments: [
      { id: "a1", name: "agreement.pdf", sizeLabel: "2.4 MB", type: "pdf" }
    ]
  },
  // Reply to the original message
  {
    id: "thread1_reply1",
    fromName: "You",
    fromDepartment: "Helpdesk",
    avatarUrl: undefined,
    subject: "Re: Please review and sign the attached agreement",
    preview: "Hi Myra, I've reviewed the agreement and have a few questions...",
    body:
      "Hi Myra,\n\nThank you for sending the agreement. I've reviewed it and have a few questions:\n\n1. The payment schedule mentions monthly installments - can we clarify the exact dates?\n2. The project timeline seems tight - is there flexibility for the deliverables?\n3. I noticed section 4.2 mentions additional terms - could you provide more details?\n\nI'm happy to discuss these points over a call if that works better for you.\n\nBest regards,\nBrian",
    dateLabel: "Jan 24",
    folder: "sent",
    labels: ["work"],
    unread: false,
    timestamp: 1706054400000, // Jan 24, 2024
    threadId: "thread1_original",
    repliedTo: "thread1_original"
  },
  // Reply to the reply
  {
    id: "thread1_reply2",
    fromName: "Myra Dudley",
    fromDepartment: "Trust and Safety",
    avatarUrl: "https://i.pravatar.cc/64?img=5",
    subject: "Re: Please review and sign the attached agreement",
    preview: "Hi Brian, thanks for your questions. Let me address them...",
    body:
      "Hi Brian,\n\nThanks for your questions. Let me address them:\n\n1. Payment dates: The installments are due on the 1st of each month, starting February 1st.\n2. Timeline flexibility: Yes, we can discuss extending the timeline if needed. What timeline would work better for you?\n3. Section 4.2: This refers to the standard confidentiality clause. I can send you a detailed explanation if needed.\n\nI'm available for a call tomorrow afternoon if you'd like to discuss further.\n\nBest regards,\nMyra",
    dateLabel: "Jan 25",
    folder: "inbox",
    labels: ["work"],
    unread: true,
    timestamp: 1706140800000, // Jan 25, 2024
    threadId: "thread1_original",
    repliedTo: "thread1_reply1"
  },
  // Another standalone message
  {
    id: "m2",
    fromName: "Shaw Murray",
    fromDepartment: "Account & Security",
    avatarUrl: "https://i.pravatar.cc/64?img=6",
    subject: "Delivery address confirmation",
    preview: "Dear Brian, dolore consectetur est cupidatat ipsum reprehenderit anim...",
    body: "Dear Brian, dolore consectetur est cupidatat ipsum reprehenderit anim quis veniam...",
    dateLabel: "Jan 25",
    folder: "inbox",
    labels: ["work"],
    unread: false,
    timestamp: 1706140800000
  },
  {
    id: "m3",
    fromName: "Zimmerman Gould",
    fromDepartment: "Sales & Marketing",
    avatarUrl: "https://i.pravatar.cc/64?img=7",
    subject: "Previous clients and their invoices",
    preview: "Dear Brian, pro ea aute dolor officia laborum id anim fugiat incididunt...",
    body: "Previous clients and their invoices body...",
    dateLabel: "Jan 24",
    folder: "inbox",
    labels: ["invoices"],
    unread: false,
    timestamp: 1706054400000
  }
];

const FOLDERS: InboxFolder[] = ["inbox", "sent", "drafts", "spam", "trash"];

export function InboxPage() {
  const [activeFolder, setActiveFolder] = useState<InboxFolder>("inbox");
  const [search, setSearch] = useState("");
  const [messages, setMessages] = useState<InboxMessage[]>(mockMessages);
  const [selectedId, setSelectedId] = useState<string | null>(mockMessages[0]?.id ?? null);
  const [isComposeOpen, setIsComposeOpen] = useState(false);
  const [composeDepartmentId, setComposeDepartmentId] = useState<string>("");
  const [composeIssue, setComposeIssue] = useState<string>("");
  const [composeJobId, setComposeJobId] = useState<string>("");
  const [composeBody, setComposeBody] = useState("");
  const [composeAttachments, setComposeAttachments] = useState<File[]>([]);
  const [showConversationHistory, setShowConversationHistory] = useState(true);
  const [isReplying, setIsReplying] = useState(false);
  const [replyToId, setReplyToId] = useState<string | null>(null);
  const copy = useTranslations(TEXTS);
  const { language } = useLanguage();
  const { data: departments = [] } = useDepartments();
  const { data: issues = [] } = useDepartmentIssues(composeDepartmentId || null, language);
  const { data: jobs = [] } = useJobs();

  const filtered = useMemo(
    () =>
      messages.filter(
        (m) =>
          m.folder === activeFolder &&
          (m.subject.toLowerCase().includes(search.toLowerCase()) ||
            m.fromName.toLowerCase().includes(search.toLowerCase()) ||
            m.preview.toLowerCase().includes(search.toLowerCase()))
      ),
    [messages, activeFolder, search]
  );

  const selected = messages.find((m) => m.id === selectedId) ?? filtered[0] ?? null;

  // Get conversation thread for the selected message
  const conversationThread = useMemo(() => {
    if (!selected) return [];
    
    // Find all messages in the same thread
    const threadId = selected.threadId || selected.id;
    const threadMessages = messages.filter(
      (m) => m.threadId === threadId || (m.id === threadId && !m.threadId)
    );
    
    // Sort by timestamp (oldest first)
    return threadMessages.sort((a, b) => {
      const timeA = a.timestamp || 0;
      const timeB = b.timestamp || 0;
      return timeA - timeB;
    });
  }, [selected, messages]);

  const selectedDepartment = departments.find((d) => d.department_id === composeDepartmentId);

  const handleFileSelect = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(e.target.files || []);
    setComposeAttachments((prev) => [...prev, ...files]);
  }, []);

  const handleRemoveAttachment = useCallback((index: number) => {
    setComposeAttachments((prev) => prev.filter((_, i) => i !== index));
  }, []);

  const formatFileSize = (bytes: number): string => {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  };

  const getFileType = (file: File): "pdf" | "image" | "other" => {
    if (file.type === "application/pdf") return "pdf";
    if (file.type.startsWith("image/")) return "image";
    return "other";
  };

  const handleSend = () => {
    if (!composeDepartmentId || !composeIssue) return;
    
    const attachments: InboxAttachment[] = composeAttachments.map((file, idx) => ({
      id: `att_${Date.now()}_${idx}`,
      name: file.name,
      sizeLabel: formatFileSize(file.size),
      type: getFileType(file)
    }));

    const departmentName = selectedDepartment?.title[language] || composeDepartmentId;
    const subjectParts = [departmentName, composeIssue];
    if (composeJobId) {
      subjectParts.push(`[Job ID: ${composeJobId}]`);
    }
    const newMsg: InboxMessage = {
      id: `local_${Date.now()}`,
      fromName: copy.compose.you,
      fromDepartment: "Helpdesk",
      subject: subjectParts.join(" - "),
      preview: composeBody.slice(0, 120) || composeIssue,
      body: composeBody || composeIssue,
      dateLabel: copy.compose.now,
      folder: "sent",
      labels: ["work"],
      unread: false,
      attachments: attachments.length > 0 ? attachments : undefined
    };
    setMessages((prev) => [newMsg, ...prev]);
    setIsComposeOpen(false);
    setComposeDepartmentId("");
    setComposeIssue("");
    setComposeJobId("");
    setComposeBody("");
    setComposeAttachments([]);
    setActiveFolder("sent");
    setSelectedId(newMsg.id);
  };

  const handleCloseCompose = () => {
    setIsComposeOpen(false);
    setIsReplying(false);
    setReplyToId(null);
    setComposeDepartmentId("");
    setComposeIssue("");
    setComposeJobId("");
    setComposeBody("");
    setComposeAttachments([]);
  };

  const handleReply = (message: InboxMessage) => {
    setIsReplying(true);
    setReplyToId(message.id);
    // Find department by name for reply
    const dept = departments.find(d => d.title[language] === message.fromDepartment);
    setComposeDepartmentId(dept?.department_id || "");
    setComposeIssue(message.subject.startsWith("Re: ") 
      ? message.subject 
      : `Re: ${message.subject}`);
    // Pre-fill reply with quoted original message
    const quotedBody = `\n\n--- Original Message ---\nFrom: ${message.fromName} (${message.fromDepartment})\nDate: ${message.dateLabel}\nSubject: ${message.subject}\n\n${message.body}`;
    setComposeBody(quotedBody);
    setIsComposeOpen(true);
  };

  const handleSendReply = () => {
    if (!replyToId || !composeBody.trim()) return;
    
    const originalMessage = messages.find(m => m.id === replyToId);
    if (!originalMessage) return;

    const threadId = originalMessage.threadId || originalMessage.id;
    const attachments: InboxAttachment[] = composeAttachments.map((file, idx) => ({
      id: `att_${Date.now()}_${idx}`,
      name: file.name,
      sizeLabel: formatFileSize(file.size),
      type: getFileType(file)
    }));

    const replyMessage: InboxMessage = {
      id: `reply_${Date.now()}`,
      fromName: copy.compose.you,
      fromDepartment: "Helpdesk",
      subject: originalMessage.subject.startsWith("Re: ") 
        ? originalMessage.subject 
        : `Re: ${originalMessage.subject}`,
      preview: composeBody.split('\n')[0].slice(0, 120) || "Reply",
      body: composeBody,
      dateLabel: copy.compose.now,
      folder: "sent",
      labels: ["work"],
      unread: false,
      attachments: attachments.length > 0 ? attachments : undefined,
      threadId: threadId,
      repliedTo: replyToId,
      timestamp: Date.now()
    };

    setMessages((prev) => [replyMessage, ...prev]);
    handleCloseCompose();
    setActiveFolder("sent");
    setSelectedId(replyMessage.id);
  };

  return (
    <div className="grid gap-4 lg:grid-cols-[360px_1fr] h-[calc(100vh-140px)]">
      {/* List column */}
      <div className="flex flex-col rounded-2xl border border-slate-200 bg-white overflow-hidden">
        <div className="px-4 py-3 border-b border-slate-100 flex items-center justify-between">
          <div className="text-sm font-semibold">{copy.sidebar.folderNames[activeFolder]}</div>
          <Button onClick={() => setIsComposeOpen(true)}>{copy.sidebar.compose}</Button>
        </div>
        <div className="px-3 py-2 border-b border-slate-100">
          <Input
            placeholder={copy.list.searchPlaceholder}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <div className="flex-1 overflow-auto">
          {filtered.map((m) => (
            <button
              key={m.id}
              type="button"
              onClick={() => setSelectedId(m.id)}
              className={cn(
                "w-full flex items-start gap-3 px-4 py-3 border-b border-slate-100 text-left hover:bg-slate-50",
                selected?.id === m.id && "bg-slate-100"
              )}
            >
              <div className="h-8 w-8 rounded-full bg-slate-200 overflow-hidden flex-shrink-0">
                {m.avatarUrl && (
                  <img src={m.avatarUrl} alt={m.fromName} className="h-full w-full object-cover" />
                )}
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between gap-2 mb-0.5">
                  <div className="text-sm font-medium text-slate-500 truncate">{m.subject}</div>
                  <div className="text-xs text-slate-400 flex-shrink-0">{m.dateLabel}</div>
                </div>
                <div className="flex items-center justify-between gap-2 mb-0.5">
                  <div className="text-xs text-slate-500 truncate">{m.fromDepartment}</div>
                </div>
              </div>
            </button>
          ))}
          {!filtered.length && (
            <div className="p-4 text-sm text-slate-500">{copy.list.empty}</div>
          )}
        </div>
      </div>

      {/* Right detail column */}
      <div className="flex flex-col rounded-2xl border border-slate-200 bg-white overflow-hidden">
        {selected ? (
          <>
            <div className="px-4 py-3 border-b border-slate-100">
              <div className="flex items-center justify-between mb-2">
                <div className="text-sm font-semibold text-slate-900">{selected.subject}</div>
                {conversationThread.length > 1 && (
                  <button
                    type="button"
                    onClick={() => setShowConversationHistory(!showConversationHistory)}
                    className="flex items-center gap-1 text-xs text-slate-600 hover:text-slate-900 px-2 py-1 rounded-lg hover:bg-slate-100"
                  >
                    <History size={14} />
                    {showConversationHistory ? copy.detail.hideHistory : copy.detail.showHistory}
                    {showConversationHistory ? <ChevronUp size={14} /> : <ChevronDown size={14} />}
                  </button>
                )}
              </div>
              {selected.labels && (
                <div className="flex flex-wrap gap-1">
                  {selected.labels.map((l) => (
                    <span
                      key={l}
                      className="text-[11px] rounded-full bg-slate-100 px-2 py-0.5 font-semibold text-slate-700"
                    >
                      {l}
                    </span>
                  ))}
                </div>
              )}
            </div>
            <div className="px-4 py-3 border-b border-slate-100 flex items-center gap-3">
              <div className="h-10 w-10 rounded-full bg-slate-200 overflow-hidden">
                {selected.avatarUrl && (
                  <img src={selected.avatarUrl} alt={selected.fromName} className="h-full w-full object-cover" />
                )}
              </div>
              <div className="flex-1 min-w-0">
                <div className="text-sm font-semibold text-slate-900">{selected.fromDepartment}</div>
                <div className="text-xs text-slate-500">{selected.fromName}</div>
              </div>
            </div>
            <div className="flex-1 overflow-auto px-4 py-4">
              {/* Conversation History */}
              {showConversationHistory && conversationThread.length > 1 && (
                <div className="mb-6">
                  <div className="text-xs font-semibold text-slate-500 mb-3 flex items-center gap-2">
                    <History size={14} />
                    {copy.detail.conversationHistory} ({conversationThread.length} {conversationThread.length === 1 ? 'message' : 'messages'})
                  </div>
                  <div className="space-y-4 border-l-2 border-slate-200 pl-4">
                    {conversationThread
                      .filter((m) => m.id !== selected.id)
                      .map((msg, idx) => (
                        <div
                          key={msg.id}
                          className={cn(
                            "rounded-xl border border-slate-200 bg-slate-50 p-3",
                            idx === 0 && "border-blue-200 bg-blue-50"
                          )}
                        >
                          <div className="flex items-center justify-between mb-2">
                            <div className="flex items-center gap-2">
                              <div className="h-6 w-6 rounded-full bg-slate-300 overflow-hidden">
                                {msg.avatarUrl && (
                                  <img src={msg.avatarUrl} alt={msg.fromName} className="h-full w-full object-cover" />
                                )}
                              </div>
                              <div>
                                <div className="text-xs font-semibold text-slate-900">{msg.fromName}</div>
                                <div className="text-[11px] text-slate-500">{msg.fromDepartment}</div>
                              </div>
                            </div>
                            <div className="text-[11px] text-slate-500">{msg.dateLabel}</div>
                          </div>
                          {idx === 0 && (
                            <div className="text-[11px] font-semibold text-blue-700 mb-2">
                              {copy.detail.originalMessage}
                            </div>
                          )}
                          <pre className="whitespace-pre-wrap text-xs text-slate-700 font-sans max-h-32 overflow-auto">
                            {msg.body.length > 200 ? `${msg.body.slice(0, 200)}...` : msg.body}
                          </pre>
                          {msg.attachments && msg.attachments.length > 0 && (
                            <div className="mt-2 text-[11px] text-slate-500">
                              {msg.attachments.length} {msg.attachments.length === 1 ? 'attachment' : 'attachments'}
                            </div>
                          )}
                        </div>
                      ))}
                  </div>
                </div>
              )}

              {/* Current Message */}
              <div className={cn(showConversationHistory && conversationThread.length > 1 && "border-t border-slate-200 pt-4")}>
                <pre className="whitespace-pre-wrap text-sm text-slate-800 font-sans">{selected.body}</pre>

                {selected.attachments && selected.attachments.length > 0 && (
                  <div className="mt-6">
                    <div className="text-xs font-semibold text-slate-500 mb-2">
                      {copy.detail.attachments.replace("{count}", selected.attachments.length.toString())}
                    </div>
                    <div className="flex flex-wrap gap-3">
                      {selected.attachments.map((a) => (
                        <button
                          key={a.id}
                          type="button"
                          className="rounded-2xl border border-slate-200 px-3 py-2 text-left text-xs hover:bg-slate-50"
                        >
                          <div className="font-semibold text-slate-800">{a.name}</div>
                          <div className="text-[11px] text-slate-500 uppercase">{a.type} · {a.sizeLabel}</div>
                        </button>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            </div>
            <div className="px-4 py-3 border-t border-slate-100 flex justify-end gap-2 text-xs">
              <Button variant="secondary" type="button" onClick={() => handleReply(selected)}>
                {copy.detail.reply}
              </Button>
              <Button variant="secondary" type="button">
                {copy.detail.replyAll}
              </Button>
              <Button variant="secondary" type="button">
                {copy.detail.forward}
              </Button>
            </div>
          </>
        ) : (
          <div className="flex-1 flex items-center justify-center text-sm text-slate-500">
            {copy.detail.empty}
          </div>
        )}
      </div>

      {isComposeOpen && (
        <div className="fixed inset-0 z-40 flex items-center justify-center bg-slate-900/40 p-4">
          <div className="w-full max-w-2xl max-h-[90vh] overflow-y-auto">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between">
                <CardTitle>{copy.compose.title}</CardTitle>
                <Button variant="ghost" type="button" onClick={handleCloseCompose} className="px-2 py-1">
                  <X size={16} />
                </Button>
              </CardHeader>
              <CardContent className="space-y-3 text-sm">
                <div className="space-y-1">
                  <label className="text-xs font-semibold text-slate-600">{copy.compose.to}</label>
                  <select
                    className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm"
                    value={composeDepartmentId}
                    onChange={(e) => {
                      setComposeDepartmentId(e.target.value);
                      setComposeIssue(""); // Reset issue when department changes
                    }}
                  >
                    <option value="">{copy.compose.selectDepartment}</option>
                    {departments.map((dept) => (
                      <option key={dept.department_id} value={dept.department_id}>
                        {dept.title[language]}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-600">{copy.compose.subject}</label>
                <select
                    className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm"
                    value={composeIssue}
                    onChange={(e) => setComposeIssue(e.target.value)}
                    disabled={!composeDepartmentId}
                  >
                    <option value="">{copy.compose.selectIssue}</option>
                    {issues.map((issue, idx) => (
                      <option key={idx} value={issue}>
                        {issue}
                      </option>
                    ))}
                  </select>
                  {!composeDepartmentId && (
                    <p className="text-[11px] text-slate-500 mt-1">{copy.compose.selectDepartmentFirst}</p>
                  )}
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-semibold text-slate-600">{copy.compose.jobId}</label>
                  <select
                    className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm"
                    value={composeJobId}
                    onChange={(e) => setComposeJobId(e.target.value)}
                  >
                    <option value="">{copy.compose.selectJobId}</option>
                    {jobs.map((job) => (
                      <option key={job.id} value={job.id}>
                        {job.id} - {job.title} {job.referenceCode ? `(${job.referenceCode})` : ""}
                      </option>
                    ))}
                  </select>
                  <p className="text-[11px] text-slate-500 mt-1">{copy.compose.jobIdHint}</p>
                </div>
                <div className="space-y-1">
                  <label className="text-xs font-semibold text-slate-600">{copy.compose.message}</label>
                  <textarea
                    className="w-full rounded-2xl border border-slate-200 px-3 py-2 text-sm min-h-[160px] resize-none"
                    value={composeBody}
                    onChange={(e) => setComposeBody(e.target.value)}
                    placeholder={copy.compose.messagePlaceholder}
                  />
                </div>
                <div className="space-y-2">
                  <label className="text-xs font-semibold text-slate-600">{copy.compose.attachments}</label>
                  <div className="flex items-center gap-2">
                    <label className="flex items-center gap-2 rounded-xl border border-slate-200 px-3 py-2 text-xs cursor-pointer hover:bg-slate-50">
                      <Paperclip size={14} />
                      {copy.compose.attachFiles}
                      <input
                        type="file"
                        multiple
                        className="hidden"
                        onChange={handleFileSelect}
                        accept=".pdf,.jpg,.jpeg,.png,.doc,.docx"
                      />
                    </label>
                  </div>
                  {composeAttachments.length > 0 && (
                    <div className="space-y-2">
                      {composeAttachments.map((file, idx) => (
                        <div
                          key={idx}
                          className="flex items-center justify-between rounded-xl border border-slate-200 px-3 py-2 text-xs"
                        >
                          <div className="flex-1 min-w-0">
                            <div className="font-semibold text-slate-800 truncate">{file.name}</div>
                            <div className="text-[11px] text-slate-500">{formatFileSize(file.size)}</div>
                          </div>
                          <Button
                            variant="ghost"
                            type="button"
                            onClick={() => handleRemoveAttachment(idx)}
                            className="px-2 py-1 ml-2"
                          >
                            <X size={14} />
                          </Button>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
                <div className="flex justify-end gap-2 pt-2">
                  <Button variant="secondary" type="button" onClick={handleCloseCompose}>
                    {copy.compose.cancel}
                  </Button>
                  <Button 
                    type="button" 
                    onClick={isReplying ? handleSendReply : handleSend} 
                    disabled={isReplying ? !composeBody.trim() : (!composeDepartmentId || !composeIssue)}
                  >
                    {copy.compose.send}
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      )}
    </div>
  );
}
