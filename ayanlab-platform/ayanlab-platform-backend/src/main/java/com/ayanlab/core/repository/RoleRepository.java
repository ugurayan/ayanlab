package com.ayanlab.core.repository;

import com.ayanlab.core.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.*;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findFirstByCode(String code);

    Optional<Role> findFirstByAppIdAndCodeAndOrganizationIdIsNull(UUID appId, String code);

    List<Role> findByAppIdIsNull();

    List<Role> findByAppIdOrderByCodeAsc(UUID appId);

    List<Role> findByIsActiveTrueOrderByCodeAsc();

    List<Role> findAllByOrderByCodeAsc();

    List<Role> findByIdInAndIsActiveTrueOrderByCodeAsc(Collection<UUID> ids);

    List<Role> findByIdInOrderByCodeAsc(Collection<UUID> ids);
}
