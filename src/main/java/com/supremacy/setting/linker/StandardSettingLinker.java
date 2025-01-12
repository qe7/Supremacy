package com.supremacy.setting.linker;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.supremacy.setting.type.Setting;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Singleton
public class StandardSettingLinker implements SettingLinker {
    private final HashMap<Setting<?>, Consumer<Setting<?>>> settingConsumerMap = new HashMap<>();

    @Inject
    public StandardSettingLinker() {
        System.out.println("StandardSettingLinker instance created");
    }

    @Override
    public void link(final Setting<?> setting, Consumer<Setting<?>> consumer) {
        this.settingConsumerMap.putIfAbsent(setting, consumer);

        System.out.println("linked setting: " + setting.getName());
    }

    @Override
    public void link(final Setting<?> setting) {
        this.settingConsumerMap.putIfAbsent(setting, (bruhmomento) -> {
            for (final Setting<?> child : setting.getChildren()) {
                if (setting.getValue() instanceof Boolean) {
                    this.settingConsumerMap.putIfAbsent(child, (childSetting) -> {});
                }
            }
        });
    }

    @Override
    public void link(final Setting<?>... settings) {
        for (final Setting<?> setting : settings) {
            this.link(setting);
        }
    }

    @Override
    public void unlink(final Setting<?> setting) {
        if (this.settingConsumerMap.containsKey(setting)) {
            this.settingConsumerMap.remove(setting);
        } else {
            System.out.println("Unable to unlink setting due to it not being found");
        }
    }

    @Override
    public void unlink(final Setting<?>[] settings) {
        for (final Setting<?> setting : settings) {
            this.unlink(setting);
        }
    }

    @Override
    public void update(final Setting<?> setting) {
        if (this.settingConsumerMap.containsKey(setting)) {
            this.settingConsumerMap.get(setting).accept(setting);
        } else {
            System.out.println("Unable to update setting due to it not being found");
        }
    }

    @Override
    public HashMap<Setting<?>, Consumer<Setting<?>>> getSettingConsumerMap() {
        return settingConsumerMap;
    }
}
