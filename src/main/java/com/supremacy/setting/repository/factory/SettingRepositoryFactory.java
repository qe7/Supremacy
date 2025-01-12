package com.supremacy.setting.repository.factory;

import com.supremacy.module.SupremacyModule;
import com.supremacy.setting.repository.SettingRepository;

public interface SettingRepositoryFactory {
    SettingRepository createSettingRepository(SupremacyModule aspireModule);
}
