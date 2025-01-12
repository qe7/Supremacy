package com.supremacy.setting.repository;

import com.google.inject.Singleton;
import com.supremacy.setting.type.Setting;

import java.util.Collection;

@Singleton
public record SettingRepositoryImpl(Collection<Setting<?>> settings) implements SettingRepository {


    @Override
    public Setting<?> getSetting(String name) {
        return settings.stream().filter(setting -> setting.getName().equals(name)).findFirst().orElse(null);
    }
}
