package com.supremacy.module;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.supremacy.module.factory.context.ModuleContextFactory;
import com.supremacy.module.type.combat.KillAuraModule;
import com.supremacy.module.type.movement.SprintModule;
import com.supremacy.module.type.render.ClickGuiModule;
import com.supremacy.module.type.render.ESPModule;
import com.supremacy.module.type.render.HudModule;
import com.supremacy.module.type.render.ModuleListModule;

import java.util.Collection;
import java.util.Optional;

public final class StandardModuleRepository implements ModuleRepository {

    private final ModuleContextFactory moduleContextFactory;

    private final Collection<ModuleContext> modules;
    private final ImmutableMap<Class<? extends SupremacyModule>, ModuleContext> moduleContextMap;

    @Inject
    public StandardModuleRepository(ModuleContextFactory moduleContextFactory) {
        this.moduleContextFactory = moduleContextFactory;
        this.modules = this.registerModule(
                HudModule.class,
                SprintModule.class,
                ModuleListModule.class,
                ClickGuiModule.class,
                KillAuraModule.class,
                ESPModule.class
        );

        final ImmutableMap.Builder<Class<? extends SupremacyModule>, ModuleContext> moduleContextMapBuilder = ImmutableMap.builder();

        for (final ModuleContext moduleContext : this.modules) {
            moduleContextMapBuilder.put(moduleContext.supremacyModule().getClass(), moduleContext);
        }

        this.moduleContextMap = moduleContextMapBuilder.build();
    }

    @Override
    public Collection<ModuleContext> getModules() {
        return modules;
    }

    @SafeVarargs
    private Collection<ModuleContext> registerModule(final Class<? extends SupremacyModule>... module) {
        final ImmutableSet.Builder<ModuleContext> modules = ImmutableSet.builder();

        for (final Class<? extends SupremacyModule> moduleClass : module) {
            modules.add(this.moduleContextFactory.create(moduleClass));
        }

        return modules.build();
    }

    @Override
    public <T extends SupremacyModule> ModuleContext getModuleContext(final Class<T> moduleClass) {
        return Optional.ofNullable(this.moduleContextMap.get(moduleClass)).orElseThrow();
    }
}
