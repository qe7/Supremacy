package com.supremacy.setting.type.clamped;

import com.supremacy.setting.type.Setting;

public interface ClampedSetting<T extends Number> extends Setting<T> {

    T getMin();

    T getMax();

    T getStep();
}