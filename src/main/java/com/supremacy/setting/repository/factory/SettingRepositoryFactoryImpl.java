package com.supremacy.setting.repository.factory;

import com.google.inject.Singleton;
import com.supremacy.module.SupremacyModule;
import com.supremacy.setting.ChildSetting;
import com.supremacy.setting.repository.SettingRepository;
import com.supremacy.setting.repository.SettingRepositoryImpl;
import com.supremacy.setting.type.Setting;
import com.supremacy.setting.type.collection.Collectable;
import com.supremacy.setting.type.collection.CollectionSetting;
import com.supremacy.setting.type.collection.ModuleMode;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;

@Singleton
public class SettingRepositoryFactoryImpl implements SettingRepositoryFactory {


    @Override
    public SettingRepository createSettingRepository(SupremacyModule aspireModule) {
        return new SettingRepositoryImpl(this.scrapeSettings(aspireModule.getClass(), aspireModule));
    }

    private Collection<Setting<?>> scrapeSettings(final Class<?> clazz, final Object instance) {
        final Collection<Setting<?>> settings = new HashSet<>();

        for (final Field declaredField : clazz.getDeclaredFields()) {
            try {
                declaredField.setAccessible(true); // Enable access to private fields
                final Object fieldValue = declaredField.get(instance);

                final boolean annotationPresent = declaredField.isAnnotationPresent(ChildSetting.class);

                if (fieldValue instanceof Setting<?> setting) {

                    if (!annotationPresent) {
                        settings.add(setting);
                    }


                    for (final Setting<?> child : setting.getChildren()) {
                        if (setting.getValue() instanceof Boolean) {
                            final Setting<Boolean> booleanSetting = (Setting<Boolean>) child;

                            if (booleanSetting.getValue()) {
                                settings.add(child);
                            }
                        }
                    }

                    if (setting instanceof CollectionSetting<?> collectionSetting) {
                        final Object settingValue = setting.getValue();

                        if (settingValue instanceof ModuleMode) {
                            for (Collectable collectable : collectionSetting.getCollection()) {
                                settings.addAll(scrapeSettings(collectable.getClass(), collectable));
                            }
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                System.err.println("Failed to access field: " + declaredField.getName());
                e.printStackTrace();
            }
        }

        return settings;
    }
}
