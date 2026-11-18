package com.ayanlab.core.repository;

import com.ayanlab.core.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findFirstByCode(String code);

    List<Permission> findByIsActiveTrueOrderByCodeAsc();

    List<Permission> findAllByOrderByCodeAsc();

    List<Permission> findByAppCodeAndIsActiveTrueOrderByCodeAsc(String appCode);

    List<Permission> findByAppCodeOrderByCodeAsc(String appCode);
}

