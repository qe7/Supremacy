package com.supremacy.setting.type.collection;

import com.supremacy.setting.type.Setting;

import java.util.Collection;

public interface CollectionSetting<T extends Collectable> extends Setting<T> {
    Collection<T> getCollection();

    void setValues(Collection<T> values);

    void getValues(Collection<T> values);

    void parseAndSet(String settingValue);
}
