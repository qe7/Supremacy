package com.supremacy.setting.linker;

import com.google.inject.Singleton;

@Singleton
public class SettingLinkerFactoryImpl implements SettingLinkerFactory {

    @Override
    public SettingLinker createSettingLinker() {
        return new StandardSettingLinker();
    }
}
