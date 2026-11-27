package com.ayanlab.core.repository;

import com.ayanlab.core.entity.ReferenceCountry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReferenceCountryRepository extends JpaRepository<ReferenceCountry, UUID> {

    Optional<ReferenceCountry> findByAlpha2Code(String alpha2Code);

    List<ReferenceCountry> findByIsActiveTrueOrderByNameAsc();

    List<ReferenceCountry> findTop50ByNameContainingIgnoreCaseAndIsActiveTrueOrderByNameAsc(String namePart);

    List<ReferenceCountry> findTop50ByRegionIgnoreCaseAndIsActiveTrueOrderByNameAsc(String region);
}