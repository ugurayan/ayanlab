package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Setting;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface SettingService {
    List<Setting> findAll(CoreContext context);
    Setting findById(UUID id, CoreContext context);
    Setting create(Setting body, CoreContext context);
    Setting update(Setting body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
