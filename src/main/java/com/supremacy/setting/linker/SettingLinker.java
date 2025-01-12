package com.supremacy.setting.linker;

import com.supremacy.setting.type.Setting;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface SettingLinker {
    void link(Setting<?> setting, Consumer<Setting<?>> consumer);

    void link(Setting<?> setting);

    void link(Setting<?>... settings);

    void unlink(Setting<?> setting);

    void unlink(Setting<?>[] settings);

    void update(final Setting<?> setting);

    HashMap<Setting<?>, Consumer<Setting<?>>> getSettingConsumerMap();
}
