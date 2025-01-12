package com.supremacy.guice;

import com.google.inject.AbstractModule;
import com.supremacy.bind.BindService;
import com.supremacy.bind.StandardBindService;
import com.supremacy.event.core.EventBus;
import com.supremacy.event.core.StandardEventBus;
import com.supremacy.event.lookup.EventIDService;
import com.supremacy.event.lookup.StandardEventIDService;
import com.supremacy.listener.BindListener;
import com.supremacy.module.ModuleRepository;
import com.supremacy.module.StandardModuleRepository;
import com.supremacy.setting.linker.SettingLinkerFactory;
import com.supremacy.setting.linker.SettingLinkerFactoryImpl;
import com.supremacy.setting.registrar.SettingRegistrar;
import com.supremacy.setting.registrar.StandardSettingRegistrar;
import com.supremacy.setting.repository.SettingRepository;
import com.supremacy.setting.repository.SettingRepositoryImpl;
import com.supremacy.setting.repository.factory.SettingRepositoryFactory;
import com.supremacy.setting.repository.factory.SettingRepositoryFactoryImpl;
import com.supremacy.toggle.ToggleService;

// Main guice module
public final class SupremacyGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        super.bind(StandardModuleRepository.class).asEagerSingleton();
        super.bind(ToggleService.class).asEagerSingleton();
        super.bind(BindService.class).to(StandardBindService.class).asEagerSingleton();

        super.bind(EventBus.class).to(StandardEventBus.class);
        super.bind(EventIDService.class).to(StandardEventIDService.class);
        super.bind(ModuleRepository.class).to(StandardModuleRepository.class).asEagerSingleton();
        super.bind(SettingRepositoryFactory.class).to(SettingRepositoryFactoryImpl.class);
    }

}