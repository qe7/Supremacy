package com.supremacy.setting.registrar;

import com.supremacy.setting.type.Setting;
import com.supremacy.setting.type.clamped.ClampedSetting;
import com.supremacy.setting.type.collection.Collectable;
import com.supremacy.setting.type.collection.CollectionSetting;

import java.util.Collection;

public interface SettingRegistrar {
    <T> Setting<T> registerSetting(final String name, final String description, final T value);

    <T> Setting<T> registerSetting(String name, T value);

    <T extends Number> ClampedSetting<T> registerClampedSetting(String name, String description, T value, T min, T max, T step);

    <T extends Number> ClampedSetting<T> registerClampedSetting(String name, T value, T min, T max, T step);

    <T extends Collectable> CollectionSetting<T> registerCollectableSetting(String name, String description, Collection<T> value);

    <T extends Collectable> CollectionSetting<T> registerCollectableSetting(String name, Collection<T> value);
}