export type TaskPriority = "low" | "medium" | "high";

export type TaskStatus = "open" | "completed";

export type TaskSection = {
  id: string;
  title: string;
  description?: string;
  order: number;
};

export type Task = {
  id: string;
  sectionId: string;
  title: string;
  notes?: string;
  dueDate?: string; // ISO string
  priority: TaskPriority;
  status: TaskStatus;
  createdAt: string;
  completedAt?: string;
  assignedWorkerId?: string; // worker id from mock workers
};
