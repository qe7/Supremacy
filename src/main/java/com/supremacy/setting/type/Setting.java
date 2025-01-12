package com.supremacy.setting.type;

import java.util.Collection;

public interface Setting<T> {
    T getValue();

    void setValue(T value);

    String getName();

    String getDescription();

    void addChild(Setting<T> child);

    Collection<Setting<T>> getChildren();
}