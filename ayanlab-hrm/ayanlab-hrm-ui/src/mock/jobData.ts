import type { JobIndustry, JobRole, Skill, DressCode, ContractType, ContactType, FreeTimeWindow, ExpertiseLevel, Certificate, JobDocument, JobTemplate } from "../types/jobs";

export const JOB_INDUSTRIES: JobIndustry[] = [
  { id: "food_beverage", name: "Food & Beverage", description: "Cafes, restaurants and kitchens" },
  { id: "retail", name: "Retail & Sales", description: "Stores, checkout and merchandising" },
  { id: "logistics", name: "Logistics & Warehouse", description: "Inventory, picking and warehouse work" },
  { id: "facilities", name: "Facilities & Maintenance", description: "Repairs and on-site maintenance" }
];

export const JOB_ROLES: JobRole[] = [
  {
    id: "barista",
    name: "Barista",
    nameKey: "roles.barista",
    description: "Prepares and serves coffee and other drinks.",
    descriptionKey: "roles.barista.description",
    categoryId: "food_beverage",
    requiredSkills: ["coffee_making", "customer_service", "cash_handling", "hygiene"]
  },
  {
    id: "waiter",
    name: "Waitress / Waiter",
    nameKey: "roles.waiter",
    description: "Takes orders and serves guests.",
    descriptionKey: "roles.waiter.description",
    categoryId: "food_beverage",
    requiredSkills: ["customer_service", "order_taking", "cash_handling", "teamwork"]
  },
  {
    id: "handyman",
    name: "Handyman",
    nameKey: "roles.handyman",
    description: "Handles basic repairs and maintenance.",
    descriptionKey: "roles.handyman.description",
    categoryId: "facilities",
    requiredSkills: ["basic_repairs", "tools", "problem_solving", "physical_fitness"]
  },
  {
    id: "kitchen",
    name: "Kitchen assistant",
    nameKey: "roles.kitchen",
    description: "Prepares food and keeps the kitchen running.",
    descriptionKey: "roles.kitchen.description",
    categoryId: "food_beverage",
    requiredSkills: ["food_preparation", "hygiene", "kitchen_equipment", "time_management"]
  },
  {
    id: "cashier",
    name: "Cashier",
    nameKey: "roles.cashier",
    description: "Handles payments and checkout.",
    descriptionKey: "roles.cashier.description",
    categoryId: "retail",
    requiredSkills: ["cash_handling", "customer_service", "accuracy", "basic_math"]
  },
  {
    id: "warehouse",
    name: "Warehouse operative",
    nameKey: "roles.warehouse",
    description: "Moves stock and keeps inventory organized.",
    descriptionKey: "roles.warehouse.description",
    categoryId: "logistics",
    requiredSkills: ["inventory_management", "heavy_lifting", "safety_procedures", "organization"]
  }
];

export const SKILLS: Skill[] = [
  // General skills
  { id: "customer_service", nameKey: "skills.customer_service", category: "general" },
  { id: "teamwork", nameKey: "skills.teamwork", category: "general" },
  { id: "communication", nameKey: "skills.communication", category: "general" },
  { id: "problem_solving", nameKey: "skills.problem_solving", category: "general" },
  { id: "time_management", nameKey: "skills.time_management", category: "general" },
  
  // Food & Beverage
  { id: "coffee_making", nameKey: "skills.coffee_making", category: "f_b" },
  { id: "food_preparation", nameKey: "skills.food_preparation", category: "f_b" },
  { id: "hygiene", nameKey: "skills.hygiene", category: "f_b" },
  { id: "kitchen_equipment", nameKey: "skills.kitchen_equipment", category: "f_b" },
  { id: "order_taking", nameKey: "skills.order_taking", category: "f_b" },
  
  // Retail & Sales
  { id: "cash_handling", nameKey: "skills.cash_handling", category: "retail" },
  { id: "sales", nameKey: "skills.sales", category: "retail" },
  { id: "inventory_management", nameKey: "skills.inventory_management", category: "retail" },
  { id: "merchandising", nameKey: "skills.merchandising", category: "retail" },
  
  // Technical & Manual
  { id: "basic_repairs", nameKey: "skills.basic_repairs", category: "technical" },
  { id: "tools", nameKey: "skills.tools", category: "technical" },
  { id: "heavy_lifting", nameKey: "skills.heavy_lifting", category: "technical" },
  { id: "safety_procedures", nameKey: "skills.safety_procedures", category: "technical" },
  { id: "organization", nameKey: "skills.organization", category: "technical" },
  
  // Office/Admin
  { id: "computer_skills", nameKey: "skills.computer_skills", category: "office" },
  { id: "accuracy", nameKey: "skills.accuracy", category: "office" },
  { id: "basic_math", nameKey: "skills.basic_math", category: "office" },
  { id: "physical_fitness", nameKey: "skills.physical_fitness", category: "physical" }
];

export const DRESS_CODES: DressCode[] = [
  {
    id: "casual",
    nameKey: "dress.casual",
    descriptionKey: "dress.casual.description",
    imageUrl: "/images/dress-casual.jpg",
    items: [
      { id: "jeans", nameKey: "dress.items.jeans", colorOptions: ["blue", "black", "gray"] },
      { id: "tshirt", nameKey: "dress.items.tshirt", colorOptions: ["white", "black", "gray", "navy"] },
      { id: "sneakers", nameKey: "dress.items.sneakers", colorOptions: ["white", "black"] }
    ]
  },
  {
    id: "smart_casual",
    nameKey: "dress.smart_casual",
    descriptionKey: "dress.smart_casual.description",
    imageUrl: "/images/dress-smart-casual.jpg",
    items: [
      { id: "chinos", nameKey: "dress.items.chinos", colorOptions: ["khaki", "navy", "black", "gray"] },
      { id: "polo_shirt", nameKey: "dress.items.polo_shirt", colorOptions: ["white", "blue", "gray"] },
      { id: "loafers", nameKey: "dress.items.loafers", colorOptions: ["brown", "black"] }
    ]
  },
  {
    id: "business",
    nameKey: "dress.business",
    descriptionKey: "dress.business.description",
    imageUrl: "/images/dress-business.jpg",
    items: [
      { id: "dress_trousers", nameKey: "dress.items.dress_trousers", colorOptions: ["black", "navy", "gray"] },
      { id: "dress_shirt", nameKey: "dress.items.dress_shirt", colorOptions: ["white", "light_blue"] },
      { id: "dress_shoes", nameKey: "dress.items.dress_shoes", colorOptions: ["black", "brown"] },
      { id: "tie", nameKey: "dress.items.tie", colorOptions: ["red", "blue", "gray", "black"] }
    ]
  },
  {
    id: "uniform_provided",
    nameKey: "dress.uniform_provided",
    descriptionKey: "dress.uniform_provided.description",
    imageUrl: "/images/dress-uniform.jpg",
    items: [
      { id: "company_shirt", nameKey: "dress.items.company_shirt", colorOptions: ["company"] },
      { id: "company_apron", nameKey: "dress.items.company_apron", colorOptions: ["company"] },
      { id: "company_hat", nameKey: "dress.items.company_hat", colorOptions: ["company"] }
    ]
  }
];

export const CONTRACT_TYPES: ContractType[] = [
  { id: "1099", nameKey: "contractTypes.1099" }, // Independent Contractor
  { id: "w2_permanent", nameKey: "contractTypes.w2_permanent" }, // Permanent (W-2)
  { id: "w2_temporary", nameKey: "contractTypes.w2_temporary" }, // Temporary (W-2)
  { id: "part_time", nameKey: "contractTypes.part_time" }, // Part-Time
  { id: "full_time", nameKey: "contractTypes.full_time" }, // Full-Time
  { id: "seasonal", nameKey: "contractTypes.seasonal" }, // Seasonal
  { id: "internship", nameKey: "contractTypes.internship" }, // Internship
  { id: "apprenticeship", nameKey: "contractTypes.apprenticeship" }, // Apprenticeship
  { id: "per_diem", nameKey: "contractTypes.per_diem" }, // Per Diem
  { id: "on_call", nameKey: "contractTypes.on_call" }, // On-Call
  { id: "temp_to_hire", nameKey: "contractTypes.temp_to_hire" } // Temp-to-Hire
];

export const CONTACT_TYPES: ContactType[] = [
  { id: "email", nameKey: "contact.email" },
  { id: "phone", nameKey: "contact.phone" },
  { id: "sms", nameKey: "contact.sms" },
  { id: "whatsapp", nameKey: "contact.whatsapp" },
  { id: "in_person", nameKey: "contact.in_person" }
];

export const FREE_TIME_WINDOWS: FreeTimeWindow[] = [
  { value: 15, unit: "minutes", labelKey: "freeTime.15min" },
  { value: 30, unit: "minutes", labelKey: "freeTime.30min" },
  { value: 45, unit: "minutes", labelKey: "freeTime.45min" },
  { value: 1, unit: "hours", labelKey: "freeTime.1hr" },
  { value: 2, unit: "hours", labelKey: "freeTime.2hr" }
];

export const EXPERTISE_LEVELS: ExpertiseLevel[] = [
  { value: 0, unit: "years", labelKey: "expertise.no_experience" },
  { value: 1, unit: "months", labelKey: "expertise.1month" },
  { value: 3, unit: "months", labelKey: "expertise.3months" },
  { value: 6, unit: "months", labelKey: "expertise.6months" },
  { value: 1, unit: "years", labelKey: "expertise.1year" },
  { value: 2, unit: "years", labelKey: "expertise.2years" },
  { value: 3, unit: "years", labelKey: "expertise.3years" },
  { value: 5, unit: "years", labelKey: "expertise.5years" },
  { value: 10, unit: "years", labelKey: "expertise.10years" }
];

// Mock API response shape: certificates list (used when VITE_USE_MOCKS=true so dropdown is populated)
export const MOCK_CERTIFICATES_LIST: { id: string; name: string }[] = [
  { id: "e6f70819-2a3b-4766-4c59-5e6f708192a3", name: "Food Handler Card" },
  { id: "food_safety_level1", name: "Food Safety Level 1" },
  { id: "food_safety_level2", name: "Food Safety Level 2" },
  { id: "cash_handling_certificate", name: "Cash Handling Certificate" },
  { id: "manual_handling", name: "Manual Handling Certificate" },
  { id: "health_hygiene", name: "Health & Hygiene Certificate" },
];

// Mock API response shape: documents list (used when VITE_USE_MOCKS=true so dropdown is populated)
export const MOCK_DOCUMENTS_LIST: { id: string; name: string }[] = [
  { id: "id_card", name: "ID Card" },
  { id: "work_permit", name: "Work Permit" },
  { id: "health_certificate", name: "Health Certificate" },
  { id: "safety_training_proof", name: "Safety Training Proof" },
  { id: "tax_id", name: "Tax ID / NI Number" },
  { id: "cv_resume", name: "CV / Resume" },
];

// Certificates related to roles
export const CERTIFICATES: Certificate[] = [
  {
    id: "food_safety_level1",
    nameKey: "certificates.food_safety_level1",
    descriptionKey: "certificates.food_safety_level1.description",
    roleIds: ["barista", "waiter", "kitchen"]
  },
  {
    id: "food_safety_level2",
    nameKey: "certificates.food_safety_level2",
    descriptionKey: "certificates.food_safety_level2.description",
    roleIds: ["kitchen"]
  },
  {
    id: "cash_handling_certificate",
    nameKey: "certificates.cash_handling",
    descriptionKey: "certificates.cash_handling.description",
    roleIds: ["cashier", "barista", "waiter"]
  },
  {
    id: "manual_handling",
    nameKey: "certificates.manual_handling",
    descriptionKey: "certificates.manual_handling.description",
    roleIds: ["warehouse", "handyman"]
  }
];

// Documents related to roles
export const DOCUMENTS: JobDocument[] = [
  {
    id: "id_card",
    nameKey: "documents.id_card",
    descriptionKey: "documents.id_card.description",
    roleIds: ["barista", "waiter", "kitchen", "cashier", "warehouse", "handyman"]
  },
  {
    id: "work_permit",
    nameKey: "documents.work_permit",
    descriptionKey: "documents.work_permit.description",
    roleIds: ["barista", "waiter", "kitchen", "cashier", "warehouse", "handyman"]
  },
  {
    id: "health_certificate",
    nameKey: "documents.health_certificate",
    descriptionKey: "documents.health_certificate.description",
    roleIds: ["kitchen", "barista", "waiter"]
  },
  {
    id: "safety_training_proof",
    nameKey: "documents.safety_training_proof",
    descriptionKey: "documents.safety_training_proof.description",
    roleIds: ["warehouse", "handyman"]
  }
];

// Predefined and organization-specific job templates
export const JOB_TEMPLATES: JobTemplate[] = [
  {
    id: "tpl_barista_morning",
    name: "Morning Barista Shift",
    description: "Standard morning coffee shop shift",
    title: "Barista - Morning shift",
    roleId: "barista",
    location: "Zurich",
    address: "Main Street 10",
    workLoad: "50-80%",
    contractTypeId: "temporary",
    contactTypeId: "email",
    freeTimeWindowValue: 30,
    freeTimeWindowUnit: "minutes",
    expertiseLabelKey: "expertise.3months",
    dressCodeId: "uniform_provided",
    requiredSkillIds: ["coffee_making", "customer_service", "cash_handling"],
    certificateIds: ["food_safety_level1", "cash_handling_certificate"],
    documentIds: ["id_card", "work_permit", "health_certificate"]
  },
  {
    id: "tpl_waiter_evening",
    name: "Restaurant Waiter Evening",
    description: "Evening service shift in busy restaurant",
    title: "Waiter / Waitress - Evening service",
    roleId: "waiter",
    location: "Basel",
    address: "Old Town 5",
    workLoad: "40-60%",
    contractTypeId: "temporary",
    contactTypeId: "phone",
    freeTimeWindowValue: 1,
    freeTimeWindowUnit: "hours",
    expertiseLabelKey: "expertise.6months",
    dressCodeId: "business",
    requiredSkillIds: ["customer_service", "order_taking", "teamwork"],
    certificateIds: ["food_safety_level1"],
    documentIds: ["id_card", "work_permit"]
  },
  {
    id: "tpl_warehouse_weekend_org1",
    orgId: "org-1",
    name: "Weekend Warehouse Helpers",
    description: "Heavy lifting and inventory support on weekends",
    title: "Warehouse helper - Weekend only",
    roleId: "warehouse",
    location: "Lucerne",
    address: "Warehouse Park 3",
    workLoad: "20-40%",
    contractTypeId: "seasonal",
    contactTypeId: "sms",
    freeTimeWindowValue: 2,
    freeTimeWindowUnit: "hours",
    expertiseLabelKey: "expertise.no_experience",
    dressCodeId: "casual",
    requiredSkillIds: ["heavy_lifting", "safety_procedures", "organization"],
    certificateIds: ["manual_handling"],
    documentIds: ["id_card", "work_permit", "safety_training_proof"]
  },
  {
    id: "tpl_cashier_pop_up_org1",
    orgId: "org-1",
    name: "Pop-up Store Cashier",
    description: "Short-term cashier for pop-up retail store",
    title: "Cashier - Pop-up store",
    roleId: "cashier",
    location: "Geneva",
    address: "Shopping Street 20",
    workLoad: "60-80%",
    contractTypeId: "temporary",
    contactTypeId: "whatsapp",
    freeTimeWindowValue: 45,
    freeTimeWindowUnit: "minutes",
    expertiseLabelKey: "expertise.1year",
    dressCodeId: "smart_casual",
    requiredSkillIds: ["cash_handling", "customer_service", "accuracy"],
    certificateIds: ["cash_handling_certificate"],
    documentIds: ["id_card", "work_permit"]
  }
];
