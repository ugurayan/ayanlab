package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, UUID> {
    Optional<UserType> findByCode(String code);
}

