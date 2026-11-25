package com.ayanlab.core.mapping;

import com.ayanlab.core.enums.ActionEnum;
import com.ayanlab.core.entity.BaseEntity;
import com.ayanlab.core.util.IdGenerator;

import java.time.Instant;
import java.util.UUID;

public class BaseMapper {

    public static  <X extends BaseEntity> X map(X input, UUID authorId, ActionEnum action ){

        if (ActionEnum.CREATE.equals(action)) {
            IdGenerator.assignIfAbsent(input);
            input.markNew();
            input.setCreatedAt( Instant.now());
            input.setCreatedBy( authorId );
            input.setIsActive(true);
        }

        if(ActionEnum.DELETE.equals(action)) {
            input.setIsActive(false);
        }
        input.setUpdatedAt(Instant.now());
        input.setUpdatedBy(authorId);
        return input;
    }

}
