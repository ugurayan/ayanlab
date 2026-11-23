package com.ayanlab.hrm.backend.service;

/**
 * Loads StaffIQ demo/mock rows from {@code staffiq-mock-data.json} (truncate then insert).
 */
public interface MockDataService {
    void reloadFromClasspathResource() throws Exception;
}