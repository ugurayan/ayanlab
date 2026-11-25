package com.ayanlab.pms.backend.entity.communication;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import com.ayanlab.pms.backend.enums.MessageStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "Message",
    schema = "ipms",
    indexes = {
        @Index(name = "idx_ipms_message_org", columnList = "organizationId"),
        @Index(name = "idx_ipms_message_recipient", columnList = "recipientProfileId")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "senderProfileId")
    private UUID senderProfileId;

    @Column(nullable = false, name = "recipientProfileId")
    private UUID recipientProfileId;

    @Column(name = "subject")
    private String subject;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MessageStatusEnum status = MessageStatusEnum.UNREAD;

    @Column(name = "sentAt")
    private Instant sentAt;

    @Column(name = "readAt")
    private Instant readAt;
}
