package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
    Optional<Menu> findByAppIdAndCode(UUID appId, String code);

    List<Menu> findByAppIdAndIsActiveTrueOrderBySortOrderAscCodeAsc(UUID appId);

    List<Menu> findByAppIdAndCodeInAndIsActiveTrueOrderBySortOrderAscCodeAsc(UUID appId, Collection<String> codes);
}
