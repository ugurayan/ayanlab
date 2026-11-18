package com.ayanlab.core.data.dto;

import com.ayanlab.core.data.enums.ResourceScopeTypeEnum;
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
