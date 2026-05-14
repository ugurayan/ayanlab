package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModuleRepository extends JpaRepository<Module, UUID> {
    Optional<Module> findByCode(String code);
    Page<Module> findByCodeContainingIgnoreCaseAndNameContainingIgnoreCase(String code, String name, Pageable pageable);
    Page<Module> findByCodeContainingIgnoreCase(String code, Pageable pageable);
    Page<Module> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
