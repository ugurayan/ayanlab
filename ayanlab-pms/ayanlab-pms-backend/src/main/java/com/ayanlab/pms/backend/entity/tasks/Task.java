package com.ayanlab.pms.backend.entity.tasks;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import com.ayanlab.pms.backend.enums.TaskPriorityEnum;
import com.ayanlab.pms.backend.enums.TaskStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "Task",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_task_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_task_assignee", columnList = "assignedToProfileId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(name = "assignedToProfileId")
    private UUID assignedToProfileId;

    @Column(nullable = false, name = "createdByProfileId")
    private UUID createdByProfileId;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskPriorityEnum priority = TaskPriorityEnum.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskStatusEnum status = TaskStatusEnum.TODO;

    @Column(name = "dueDate")
    private LocalDate dueDate;

    @Column(name = "completedAt")
    private Instant completedAt;
}
