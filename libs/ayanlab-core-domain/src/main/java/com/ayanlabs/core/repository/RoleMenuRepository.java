package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.RoleMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface RoleMenuRepository extends JpaRepository<RoleMenu, UUID> {
    boolean existsByRoleIdAndMenuId(UUID roleId, UUID menuId);

    List<RoleMenu> findByRoleIdInAndIsActiveTrue(Collection<UUID> roleIds);
}
