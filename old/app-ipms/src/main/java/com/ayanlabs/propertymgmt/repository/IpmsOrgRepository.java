package com.ayanlabs.propertymgmt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface IpmsOrgRepository<T, ID> extends JpaRepository<T, ID> {

    List<T> findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(UUID organizationId);

    Optional<T> findByIdAndOrganizationId(UUID id, UUID organizationId);
}
