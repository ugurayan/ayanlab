package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.ApplicationPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicationPermissionRepository extends JpaRepository<ApplicationPermission, UUID> {
}

