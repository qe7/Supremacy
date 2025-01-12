package com.supremacy.setting.type.collection;

import com.supremacy.setting.type.Setting;

import java.util.Collection;
import java.util.List;

public class CollectionSettingImpl<T extends Collectable> implements CollectionSetting<T> {


    private Collection<T> values;

    private T value;

    private final String name, description;

    public CollectionSettingImpl(String name, String description, final Collection<T> values) {
        this.name = name;
        this.description = description;
        this.values = values;
    }


    public CollectionSettingImpl(String name, final Collection<T> values) {
        this.name = name;
        this.description = "No description provided.";
        this.values = values;
    }

    @Override
    public Collection<T> getCollection() {
        return this.values;
    }

    @Override
    public void setValues(Collection<T> values) {
        this.values = values;
    }

    @Override
    public void getValues(Collection<T> values) {
        this.values = values;
    }

    @Override
    public void parseAndSet(String settingValue) {
        for (final T value : this.values) {
            if (value.getName().equalsIgnoreCase(settingValue)) {
                this.setValue(value);
                return;
            }
        }
        System.out.println("Error Parsing in setting " + this.getName() + ", could not find the value " + settingValue);
    }

    @Override
    public T getValue() {
        if (value == null) {
            return this.values.stream().findFirst().orElse(null);
        }

        return value;
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

    @Override
    public void addChild(Setting<T> child) {

    }

    @Override
    public Collection<Setting<T>> getChildren() {
        return List.of();
    }
}
