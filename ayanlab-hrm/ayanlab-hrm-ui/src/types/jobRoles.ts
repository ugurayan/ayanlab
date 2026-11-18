export interface JobRoleCategory {
  id: string;
  name: string;
  description?: string;
  createdAt: number;
  createdBy: string;
  updatedAt: number;
  updatedBy: string;
}

export interface JobRole {
  id: string;
  name: string;
  description: string;
  categoryId: string;
  requiredSkills: string[];
  isActive: boolean;
  createdAt: number;
  createdBy: string;
  updatedAt: number;
  updatedBy: string;
  category?: JobRoleCategory;
}

export interface CreateJobRoleDto {
  name: string;
  description: string;
  categoryId: string;
  requiredSkills: string[];
  isActive: boolean;
}

export interface UpdateJobRoleDto extends Partial<CreateJobRoleDto> {}

export interface CreateJobRoleCategoryDto {
  name: string;
  description?: string;
}

export interface UpdateJobRoleCategoryDto extends Partial<CreateJobRoleCategoryDto> {}
