package com.ayanlab.core.dto;

import com.ayanlab.core.enums.ResourceScopeTypeEnum;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceScopeDto {

    private ResourceScopeTypeEnum resourceType;
    private UUID resourceId;
    private UUID organizationId;
}
