package com.supremacy.setting.repository;

import com.supremacy.setting.type.Setting;

import java.util.Collection;

public interface SettingRepository {
    Setting<?> getSetting(String name);

    Collection<Setting<?>> settings();
}
