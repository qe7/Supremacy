package com.supremacy.setting.type.clamped;

import com.supremacy.setting.type.Setting;

import java.util.Collection;
import java.util.List;

public class ClampedSettingImpl<T extends Number> implements ClampedSetting<T> {

    private final T min;
    private final T max;
    private final T step;
    private T value;

    private final String name, description;

    public ClampedSettingImpl(String name, String description, T min, T max, T value, T step) {
        this.name = name;
        this.description = description;

        this.min = min;
        this.max = max;
        this.step = step;
        this.value = value;
    }

    public ClampedSettingImpl(String name, T min, T max, T value, T step) {
        this(name, "No description provided", min, max, value, step);
    }

    @Override
    public T getMin() {
        return min;
    }

    @Override
    public T getMax() {
        return max;
    }

    @Override
    public T getStep() {
        return step;
    }

    @Override
    public T getValue() {
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
