package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Setting;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface SettingService {
    List<Setting> findAll(CoreContext context);
    Setting findById(String id, CoreContext context);
    Setting create(Setting body, CoreContext context);
    Setting update(Setting body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
