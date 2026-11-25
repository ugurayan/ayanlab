package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.Certificate;
import com.ayanlab.hrm.backend.entity.ContactType;
import com.ayanlab.hrm.backend.entity.ContractType;
import com.ayanlab.hrm.backend.entity.Department;
import com.ayanlab.hrm.backend.entity.Document;
import com.ayanlab.hrm.backend.entity.DressCode;
import com.ayanlab.hrm.backend.entity.ExpertiseLevel;
import com.ayanlab.hrm.backend.entity.FreeTimeWindow;
import com.ayanlab.hrm.backend.entity.JobDocument;
import com.ayanlab.hrm.backend.entity.JobTemplate;
import com.ayanlab.hrm.backend.entity.DocumentVerification;
import com.ayanlab.hrm.backend.entity.Education;
import com.ayanlab.hrm.backend.entity.FinanceRecord;
import com.ayanlab.hrm.backend.entity.Job;
import com.ayanlab.hrm.backend.entity.JobRole;
import com.ayanlab.hrm.backend.entity.JobRoleCategory;
import com.ayanlab.hrm.backend.entity.MockRecord;
import com.ayanlab.hrm.backend.entity.Profile;
import com.ayanlab.hrm.backend.entity.ProfileContact;
import com.ayanlab.hrm.backend.entity.ProfileLanguage;
import com.ayanlab.hrm.backend.entity.ProfileLocationDetail;
import com.ayanlab.hrm.backend.entity.Skill;
import com.ayanlab.hrm.backend.entity.SystemAudit;
import com.ayanlab.hrm.backend.entity.Task;
import com.ayanlab.hrm.backend.entity.TransactionRecord;
import com.ayanlab.hrm.backend.entity.UserAudit;
import com.ayanlab.hrm.backend.entity.WorkExperience;
import com.ayanlab.hrm.backend.entity.WorkerCertificate;
import com.ayanlab.hrm.backend.entity.WorkerSkill;
import com.ayanlab.hrm.backend.repository.CertificateRepository;
import com.ayanlab.hrm.backend.repository.ContactTypeRepository;
import com.ayanlab.hrm.backend.repository.ContractTypeRepository;
import com.ayanlab.hrm.backend.repository.DepartmentRepository;
import com.ayanlab.hrm.backend.repository.DressCodeRepository;
import com.ayanlab.hrm.backend.repository.ExpertiseLevelRepository;
import com.ayanlab.hrm.backend.repository.FreeTimeWindowRepository;
import com.ayanlab.hrm.backend.repository.JobDocumentRepository;
import com.ayanlab.hrm.backend.repository.JobTemplateRepository;
import com.ayanlab.hrm.backend.repository.DocumentRepository;
import com.ayanlab.hrm.backend.repository.DocumentVerificationRepository;
import com.ayanlab.hrm.backend.repository.EducationRepository;
import com.ayanlab.hrm.backend.repository.FinanceRecordRepository;
import com.ayanlab.hrm.backend.repository.JobRepository;
import com.ayanlab.hrm.backend.repository.JobRoleCategoryRepository;
import com.ayanlab.hrm.backend.repository.JobRoleRepository;
import com.ayanlab.hrm.backend.repository.MockRecordRepository;
import com.ayanlab.hrm.backend.repository.ProfileContactRepository;
import com.ayanlab.hrm.backend.repository.ProfileLanguageRepository;
import com.ayanlab.hrm.backend.repository.ProfileLocationDetailRepository;
import com.ayanlab.hrm.backend.repository.ProfileRepository;
import com.ayanlab.hrm.backend.repository.SkillRepository;
import com.ayanlab.hrm.backend.repository.SystemAuditRepository;
import com.ayanlab.hrm.backend.repository.TaskRepository;
import com.ayanlab.hrm.backend.repository.TransactionRecordRepository;
import com.ayanlab.hrm.backend.repository.UserAuditRepository;
import com.ayanlab.hrm.backend.repository.WorkExperienceRepository;
import com.ayanlab.hrm.backend.repository.WorkerCertificateRepository;
import com.ayanlab.hrm.backend.repository.WorkerSkillRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.ayanlab.hrm.backend.mapping.MockDataJsonMapper;
/** Maps {@code staffiq-mock-data.json} into JPA entities for CRUD APIs. */
@Component
@RequiredArgsConstructor
public class MockDataSeeder {

    private static final List<String> REMAINING_MOCK_COLLECTIONS =
            List.of("verificationRequests");

    private final JobRepository jobRepository;
    private final TaskRepository taskRepository;
    private final DocumentRepository documentRepository;
    private final FinanceRecordRepository financeRecordRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final UserAuditRepository userAuditRepository;
    private final SystemAuditRepository systemAuditRepository;
    private final DocumentVerificationRepository documentVerificationRepository;
    private final DepartmentRepository departmentRepository;
    private final ProfileRepository profileRepository;
    private final JobRoleRepository jobRoleRepository;
    private final JobRoleCategoryRepository jobRoleCategoryRepository;
    private final SkillRepository skillRepository;
    private final CertificateRepository certificateRepository;
    private final ContractTypeRepository contractTypeRepository;
    private final ContactTypeRepository contactTypeRepository;
    private final DressCodeRepository dressCodeRepository;
    private final FreeTimeWindowRepository freeTimeWindowRepository;
    private final ExpertiseLevelRepository expertiseLevelRepository;
    private final JobDocumentRepository jobDocumentRepository;
    private final JobTemplateRepository jobTemplateRepository;
    private final MockRecordRepository mockRecordRepository;
    private final EducationRepository educationRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final ProfileLanguageRepository profileLanguageRepository;
    private final ProfileContactRepository profileContactRepository;
    private final ProfileLocationDetailRepository profileLocationDetailRepository;
    private final WorkerSkillRepository workerSkillRepository;
    private final WorkerCertificateRepository workerCertificateRepository;

    public void truncateCrudTables() {
        workerCertificateRepository.deleteAllInBatch();
        workerSkillRepository.deleteAllInBatch();
        profileLocationDetailRepository.deleteAllInBatch();
        profileContactRepository.deleteAllInBatch();
        profileLanguageRepository.deleteAllInBatch();
        workExperienceRepository.deleteAllInBatch();
        educationRepository.deleteAllInBatch();
        documentVerificationRepository.deleteAllInBatch();
        documentRepository.deleteAllInBatch();
        financeRecordRepository.deleteAllInBatch();
        transactionRecordRepository.deleteAllInBatch();
        userAuditRepository.deleteAllInBatch();
        systemAuditRepository.deleteAllInBatch();
        taskRepository.deleteAllInBatch();
        jobRepository.deleteAllInBatch();
        departmentRepository.deleteAllInBatch();
        profileRepository.deleteAllInBatch();
        jobTemplateRepository.deleteAllInBatch();
        jobDocumentRepository.deleteAllInBatch();
        expertiseLevelRepository.deleteAllInBatch();
        freeTimeWindowRepository.deleteAllInBatch();
        dressCodeRepository.deleteAllInBatch();
        contactTypeRepository.deleteAllInBatch();
        contractTypeRepository.deleteAllInBatch();
        certificateRepository.deleteAllInBatch();
        skillRepository.deleteAllInBatch();
        jobRoleRepository.deleteAllInBatch();
        jobRoleCategoryRepository.deleteAllInBatch();
        mockRecordRepository.deleteAllInBatch();
    }

    public void seedFromRoot(JsonNode root) {
        loadJobs(root.path("jobs"));
        loadTasks(root.path("tasks"));
        loadDocuments(root.path("documents"));
        loadFinanceFromInvoices(root.path("invoices"));
        loadTransactions(root.path("transactions"));
        loadUserAudits(root.path("auditUser"));
        loadSystemAudits(root.path("auditSystem"), root.path("audit"));
        loadDocumentVerifications(root.path("verificationTasks"), root.path("verificationRequests"));
        loadDepartments(root.path("departments"));
        loadProfiles(root.path("jobProfiles"));
        loadJobCatalog(root.path("jobCatalog"));
        loadProfileDomain(root);

        for (String collection : REMAINING_MOCK_COLLECTIONS) {
            loadMockCollection(collection, root.path(collection));
        }
    }

    private void loadJobs(JsonNode nodes) {
        if (!nodes.isArray()) {
            return;
        }
        List<Job> rows = new ArrayList<>();
        for (JsonNode n : nodes) {
            rows.add(MockDataJsonMapper.jobFrom(n));
        }
        jobRepository.saveAll(rows);
    }

    private void loadTasks(JsonNode nodes) {
        if (!nodes.isArray()) {
            return;
        }
        List<Task> rows = new ArrayList<>();
        for (JsonNode n : nodes) {
            rows.add(MockDataJsonMapper.taskFrom(n));
        }
        taskRepository.saveAll(rows);
    }

    private void loadDocuments(JsonNode nodes) {
        if (!nodes.isArray()) {
            return;
        }
        List<Document> rows = new ArrayList<>();
        for (JsonNode n : nodes) {
            rows.add(MockDataJsonMapper.documentFrom(n));
        }
        documentRepository.saveAll(rows);
    }

    private void loadFinanceFromInvoices(JsonNode nodes) {
        if (!nodes.isArray()) {
            return;
        }
        List<FinanceRecord> rows = new ArrayList<>();
        for (JsonNode n : nodes) {
            rows.add(MockDataJsonMapper.financeFromInvoice(n));
        }
        financeRecordRepository.saveAll(rows);
    }

    private void loadTransactions(JsonNode nodes) {
        if (!nodes.isArray()) {
            return;
        }
        List<TransactionRecord> rows = new ArrayList<>();
        for (JsonNode n : nodes) {
            rows.add(MockDataJsonMapper.transactionFrom(n));
        }
        transactionRecordRepository.saveAll(rows);
    }

    private void loadUserAudits(JsonNode nodes) {
        if (!nodes.isArray()) {
            return;
        }
        List<UserAudit> rows = new ArrayList<>();
        for (JsonNode n : nodes) {
            rows.add(MockDataJsonMapper.userAuditFrom(n));
        }
        userAuditRepository.saveAll(rows);
    }

    private void loadSystemAudits(JsonNode system, JsonNode legacy) {
        List<SystemAudit> rows = new ArrayList<>();
        if (system.isArray()) {
            for (JsonNode n : system) {
                rows.add(MockDataJsonMapper.systemAuditFrom(n));
            }
        }
        if (legacy.isArray()) {
            for (JsonNode n : legacy) {
                SystemAudit row = MockDataJsonMapper.systemAuditFrom(n);
                if (row.getId() != null && rows.stream().noneMatch(r -> row.getId().equals(r.getId()))) {
                    rows.add(row);
                }
            }
        }
        systemAuditRepository.saveAll(rows);
    }

    private void loadDocumentVerifications(JsonNode tasks, JsonNode requests) {
        List<DocumentVerification> rows = new ArrayList<>();
        if (tasks.isArray()) {
            for (JsonNode n : tasks) {
                rows.add(MockDataJsonMapper.verificationFrom(n));
            }
        }
        if (requests.isArray()) {
            for (JsonNode n : requests) {
                rows.add(MockDataJsonMapper.verificationFrom(n));
            }
        }
        documentVerificationRepository.saveAll(rows);
    }

    private void loadDepartments(JsonNode nodes) {
        if (!nodes.isArray()) {
            return;
        }
        List<Department> rows = new ArrayList<>();
        int i = 0;
        for (JsonNode n : nodes) {
            Department row = new Department();
            String id = n.path("department_id").asText("dept_" + i);
            row.setId(com.ayanlab.hrm.backend.util.Ids.parse(id));
            row.setDetails(n);
            rows.add(row);
            i++;
        }
        departmentRepository.saveAll(rows);
    }

    private void loadProfiles(JsonNode nodes) {
        if (!nodes.isArray()) {
            return;
        }
        List<Profile> rows = new ArrayList<>();
        for (JsonNode n : nodes) {
            Profile row = new Profile();
            row.setId(com.ayanlab.hrm.backend.util.Ids.parse(text(n, "id")));
            row.setWorkerId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "workerId")));
            row.setProfileId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "id")));
            row.setDetails(n);
            rows.add(row);
        }
        profileRepository.saveAll(rows);
    }

    private void loadProfileDomain(JsonNode root) {
        loadProfileLinked(root.path("profileEducations"), educationRepository, Education.class);
        loadProfileLinked(root.path("profileExperiences"), workExperienceRepository, WorkExperience.class);
        loadProfileLinked(root.path("profileLanguages"), profileLanguageRepository, ProfileLanguage.class);
        loadProfileLinked(root.path("profileContacts"), profileContactRepository, ProfileContact.class);
        loadProfileLinked(
                root.path("profileLocationDetails"), profileLocationDetailRepository, ProfileLocationDetail.class);
        loadProfileLinked(root.path("workerSkills"), workerSkillRepository, WorkerSkill.class);
        loadProfileLinked(root.path("workerCertificates"), workerCertificateRepository, WorkerCertificate.class);
    }

    private <T extends com.ayanlab.hrm.backend.entity.ProfileLinkedEntity> void loadProfileLinked(
            JsonNode nodes,
            org.springframework.data.jpa.repository.JpaRepository<T, java.util.UUID> repository,
            Class<T> type) {
        if (!nodes.isArray()) {
            return;
        }
        List<T> rows = new ArrayList<>();
        for (JsonNode n : nodes) {
            try {
                T row = type.getDeclaredConstructor().newInstance();
                if (row instanceof Education education) {
                    education.setId(com.ayanlab.hrm.backend.util.Ids.parse(text(n, "id")));
                    education.setWorkerId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "workerId")));
                    education.setProfileId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "profileId")));
                    education.setDetails(n);
                    rows.add(type.cast(education));
                } else if (row instanceof WorkExperience experience) {
                    experience.setId(com.ayanlab.hrm.backend.util.Ids.parse(text(n, "id")));
                    experience.setWorkerId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "workerId")));
                    experience.setProfileId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "profileId")));
                    experience.setDetails(n);
                    rows.add(type.cast(experience));
                } else if (row instanceof ProfileLanguage language) {
                    language.setId(com.ayanlab.hrm.backend.util.Ids.parse(text(n, "id")));
                    language.setWorkerId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "workerId")));
                    language.setProfileId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "profileId")));
                    language.setDetails(n);
                    rows.add(type.cast(language));
                } else if (row instanceof ProfileContact contact) {
                    contact.setId(com.ayanlab.hrm.backend.util.Ids.parse(text(n, "id")));
                    contact.setWorkerId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "workerId")));
                    contact.setProfileId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "profileId")));
                    contact.setDetails(n);
                    rows.add(type.cast(contact));
                } else if (row instanceof ProfileLocationDetail location) {
                    location.setId(com.ayanlab.hrm.backend.util.Ids.parse(text(n, "id")));
                    location.setWorkerId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "workerId")));
                    location.setProfileId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "profileId")));
                    location.setDetails(n);
                    rows.add(type.cast(location));
                } else if (row instanceof WorkerSkill skill) {
                    skill.setId(com.ayanlab.hrm.backend.util.Ids.parse(text(n, "id")));
                    skill.setWorkerId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "workerId")));
                    skill.setProfileId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "profileId")));
                    skill.setDetails(n);
                    rows.add(type.cast(skill));
                } else if (row instanceof WorkerCertificate certificate) {
                    certificate.setId(com.ayanlab.hrm.backend.util.Ids.parse(text(n, "id")));
                    certificate.setWorkerId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "workerId")));
                    certificate.setProfileId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "profileId")));
                    certificate.setDetails(n);
                    rows.add(type.cast(certificate));
                }
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Cannot seed " + type.getSimpleName(), e);
            }
        }
        if (!rows.isEmpty()) {
            repository.saveAll(rows);
        }
    }

    private void loadJobCatalog(JsonNode catalog) {
        if (catalog.isMissingNode() || !catalog.isObject()) {
            return;
        }
        saveCatalog(catalog.path("jobRoleCategories"), JobRoleCategory.class, jobRoleCategoryRepository);
        saveCatalog(catalog.path("jobRoles"), JobRole.class, jobRoleRepository);
        saveCatalog(catalog.path("skills"), Skill.class, skillRepository);
        saveCatalog(
                List.of(catalog.path("certificates"), catalog.path("mockCertificatesList")),
                Certificate.class,
                certificateRepository);
        saveCatalog(
                List.of(catalog.path("documents"), catalog.path("mockDocumentsList")),
                JobDocument.class,
                jobDocumentRepository);
        saveCatalog(catalog.path("dressCodes"), DressCode.class, dressCodeRepository);
        saveCatalog(catalog.path("contractTypes"), ContractType.class, contractTypeRepository);
        saveCatalog(catalog.path("contactTypes"), ContactType.class, contactTypeRepository);
        saveCatalog(catalog.path("freeTimeWindows"), FreeTimeWindow.class, freeTimeWindowRepository);
        saveCatalog(catalog.path("expertiseLevels"), ExpertiseLevel.class, expertiseLevelRepository);
        saveCatalog(catalog.path("jobTemplates"), JobTemplate.class, jobTemplateRepository);

        ObjectNode remainder = catalog.deepCopy();
        for (String key : List.of(
                "jobRoleCategories",
                "jobRoles",
                "skills",
                "certificates",
                "mockCertificatesList",
                "documents",
                "mockDocumentsList",
                "dressCodes",
                "contractTypes",
                "contactTypes",
                "freeTimeWindows",
                "expertiseLevels",
                "jobTemplates")) {
            remainder.remove(key);
        }
        if (!remainder.isEmpty()) {
            mockRecordRepository.save(mockRecord("jobCatalog", "reference", remainder));
        }
    }

    private <T> void saveCatalog(JsonNode nodes, Class<T> type, JpaRepository<T, java.util.UUID> repository) {
        saveCatalog(List.of(nodes), type, repository);
    }

    /** Persists catalog rows. Rows without {@code id} use {@code labelKey}. Duplicate ids are skipped. */
    private <T> void saveCatalog(List<JsonNode> sources, Class<T> type, JpaRepository<T, java.util.UUID> repository) {
        Set<String> seen = new HashSet<>();
        List<T> rows = new ArrayList<>();
        for (JsonNode nodes : sources) {
            if (!nodes.isArray()) {
                continue;
            }
            for (JsonNode n : nodes) {
                if (!(n instanceof ObjectNode obj)) {
                    continue;
                }
                String id = resolveCatalogId(obj);
                if (!seen.add(id)) {
                    continue;
                }
                obj.put("id", id);
                try {
                    T row = type.getDeclaredConstructor().newInstance();
                    type.getMethod("setId", java.util.UUID.class).invoke(row, com.ayanlab.hrm.backend.util.Ids.parse(id));
                    type.getMethod("setDetails", JsonNode.class).invoke(row, obj);
                    String orgId = textOrNull(obj, "orgId");
                    if (orgId != null) {
                        type.getMethod("setOrgId", java.util.UUID.class).invoke(row, com.ayanlab.hrm.backend.util.Ids.parse(orgId));
                    }
                    rows.add(row);
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException("Cannot seed " + type.getSimpleName(), e);
                }
            }
        }
        if (!rows.isEmpty()) {
            repository.saveAll(rows);
        }
    }

    private static String resolveCatalogId(ObjectNode obj) {
        String id = textOrNull(obj, "id");
        if (id != null) {
            return id;
        }
        String labelKey = textOrNull(obj, "labelKey");
        if (labelKey != null) {
            return labelKey;
        }
        throw new IllegalStateException("Catalog row is missing id and labelKey");
    }

    private void loadMockCollection(String collection, JsonNode node) {
        if (node.isMissingNode() || node.isNull()) {
            return;
        }
        List<MockRecord> batch = new ArrayList<>();
        if (node.isArray()) {
            int i = 0;
            for (JsonNode item : node) {
                String key = item.path("id").asText(
                        item.path("department_id").asText("row_" + i));
                batch.add(mockRecord(collection, key, item));
                i++;
            }
        } else if (node.isObject()) {
            batch.add(mockRecord(collection, "bundle", node));
        }
        mockRecordRepository.saveAll(batch);
    }

    private static MockRecord mockRecord(String collection, String recordKey, JsonNode payload) {
        MockRecord row = new MockRecord();
        row.setCollection(collection);
        row.setRecordKey(recordKey);
        row.setId(collection + ":" + recordKey);
        row.setPayload(payload);
        return row;
    }

    private static String text(JsonNode n, String field) {
        return n.path(field).asText();
    }

    private static String textOrNull(JsonNode n, String field) {
        JsonNode v = n.path(field);
        return v.isMissingNode() || v.isNull() || v.asText().isBlank() ? null : v.asText();
    }
}