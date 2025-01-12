package com.supremacy.setting.registrar;

import com.google.inject.Singleton;
import com.supremacy.setting.type.Setting;
import com.supremacy.setting.type.SettingImpl;
import com.supremacy.setting.type.clamped.ClampedSetting;
import com.supremacy.setting.type.clamped.ClampedSettingImpl;
import com.supremacy.setting.type.collection.Collectable;
import com.supremacy.setting.type.collection.CollectionSetting;
import com.supremacy.setting.type.collection.CollectionSettingImpl;

import java.util.Collection;

@Singleton
public class StandardSettingRegistrar implements SettingRegistrar {

    @Override
    public <T> Setting<T> registerSetting(String name, String description, T value) {
        return new SettingImpl<>(name, description, value);
    }

    @Override
    public <T> Setting<T> registerSetting(String name, T value) {
        return new SettingImpl<>(name, value);
    }

    @Override
    public <T extends Number> ClampedSetting<T> registerClampedSetting(String name, String description, T value, T min, T max, T step) {

        //String name, String description, T min, T max, T value, T step
        return new ClampedSettingImpl<>(name, description, min, max, value, step);
    }

    @Override
    public <T extends Number> ClampedSetting<T> registerClampedSetting(String name, T value, T min, T max, T step) {
        //String name, String description, T min, T max, T value, T step
        return new ClampedSettingImpl<>(name, min, max, value, step);
    }

    @Override
    public <T extends Collectable> CollectionSetting<T> registerCollectableSetting(String name, String description, Collection<T> value) {
        return new CollectionSettingImpl<>(name, description, value);
    }

    @Override
    public <T extends Collectable> CollectionSetting<T> registerCollectableSetting(String name, Collection<T> value) {
        return new CollectionSettingImpl<>(name, value);
    }

}
