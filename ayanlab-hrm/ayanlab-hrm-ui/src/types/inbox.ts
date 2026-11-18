export type InboxFolder =
  | "inbox"
  | "sent"
  | "drafts"
  | "spam"
  | "trash";

export type InboxLabel = "personal" | "work" | "payments" | "invoices" | "accounts" | "forums";

export type InboxAttachment = {
  id: string;
  name: string;
  sizeLabel: string; // e.g. "15.5 KB"
  type: "pdf" | "image" | "other";
};

export type InboxMessage = {
  id: string;
  fromName: string;
  fromDepartment: string;
  avatarUrl?: string;
  subject: string;
  preview: string;
  body: string;
  dateLabel: string; // e.g. "Jan 25"
  folder: InboxFolder;
  labels?: InboxLabel[];
  unread?: boolean;
  starred?: boolean;
  attachments?: InboxAttachment[];
  // Threading support
  threadId?: string; // ID of the original message in the thread
  repliedTo?: string; // ID of the message this is replying to
  timestamp?: number; // Unix timestamp for sorting
};
