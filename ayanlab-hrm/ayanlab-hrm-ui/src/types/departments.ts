export type Department = {
  department_id: string;
  title: {
    en: string;
    de: string;
    tr: string;
  };
  issues: {
    en: string[];
    de: string[];
    tr: string[];
  };
};

export type DepartmentIssue = {
  departmentId: string;
  issue: string;
};

