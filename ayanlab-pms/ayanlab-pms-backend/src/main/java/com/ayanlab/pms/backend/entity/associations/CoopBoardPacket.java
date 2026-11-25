package com.ayanlab.pms.backend.entity.associations;

import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.pms.backend.dto.OrgScoped;
import com.ayanlab.pms.backend.enums.CoopBoardPacketStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "CoopBoardPacket",
    schema = "ipms",
    indexes = @Index(name = "idx_ipms_coop_board_org", columnList = "organizationId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoopBoardPacket extends BaseEntity implements OrgScoped {

    @Column(nullable = false, name = "organizationId")
    private UUID organizationId;

    @Column(nullable = false, name = "associationId")
    private UUID associationId;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(name = "meetingDate")
    private LocalDate meetingDate;

    @Column(columnDefinition = "text", name = "agendaJson")
    private String agendaJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CoopBoardPacketStatusEnum status = CoopBoardPacketStatusEnum.DRAFT;
}