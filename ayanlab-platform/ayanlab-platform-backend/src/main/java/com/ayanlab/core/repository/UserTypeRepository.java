package com.ayanlab.core.repository;

import com.ayanlab.core.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, UUID> {
    Optional<UserType> findByCode(String code);

    List<UserType> findByIsActiveTrueOrderByCodeAsc();
}

