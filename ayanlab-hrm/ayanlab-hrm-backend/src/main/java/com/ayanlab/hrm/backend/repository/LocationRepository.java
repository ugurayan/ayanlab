package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, String> {}