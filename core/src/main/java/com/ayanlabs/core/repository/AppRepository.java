package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.App;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface AppRepository extends JpaRepository<App, UUID> {
    Optional<App> findByCode(String code);

    Page<App> findByCodeContainingIgnoreCaseAndNameContainingIgnoreCase(String code, String name, Pageable pageable);

    Page<App> findByCodeContainingIgnoreCase(String code, Pageable pageable);

    Page<App> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
