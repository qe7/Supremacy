package com.supremacy.setting.type;

import java.util.ArrayList;
import java.util.Collection;

public class SettingImpl<T> implements Setting<T> {

    private T value;

    private final String name, description;

    private final Collection<Setting<T>> children = new ArrayList<>();

    public SettingImpl(String name, String description, T value) {
        this.value = value;
        this.name = name;
        this.description = description;
    }

    public SettingImpl(String name, T value) {
        this.value = value;
        this.name = name;
        this.description = "No description provided";
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public void addChild(Setting<T> child) {
        this.children.add(child);
    }

    public Collection<Setting<T>> getChildren() {
        return this.children;
    }
}