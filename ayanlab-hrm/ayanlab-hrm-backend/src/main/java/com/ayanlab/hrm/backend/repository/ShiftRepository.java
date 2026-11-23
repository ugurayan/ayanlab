package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, String> {}