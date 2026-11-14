package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.*;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findFirstByCode(String code);

    List<Role> findByIsActiveTrueOrderByCodeAsc();

    List<Role> findByIdInAndIsActiveTrueOrderByCodeAsc(Collection<UUID> ids);
}
