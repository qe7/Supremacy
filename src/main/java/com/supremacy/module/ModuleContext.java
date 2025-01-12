package com.supremacy.module;

import com.supremacy.setting.repository.SettingRepository;

public record ModuleContext(ModuleData moduleContext, SupremacyModule supremacyModule, SettingRepository settingRepository) { }
