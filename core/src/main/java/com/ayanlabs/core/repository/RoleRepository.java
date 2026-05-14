package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findFirstByCode(String code);
}
