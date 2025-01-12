package com.supremacy.mixin;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.supremacy.bind.BindService;
import com.supremacy.event.core.EventBus;
import com.supremacy.event.events.type.game.GameTickEvent;
import com.supremacy.event.events.type.key.KeyPressEvent;
import com.supremacy.listener.BindListener;
import com.supremacy.module.StandardModuleRepository;
import com.supremacy.module.type.movement.SprintModule;
import com.supremacy.module.type.render.HudModule;
import com.supremacy.module.type.render.ModuleListModule;
import com.supremacy.provider.provider.InjectorProvider;
import com.supremacy.toggle.ToggleService;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        final Provider<Injector> injectorProvider = InjectorProvider.getInstance();

        final Injector injector = injectorProvider.get();

        final StandardModuleRepository standardModuleRepository = injector.getInstance(StandardModuleRepository.class);

        final ToggleService toggleService = injector.getInstance(ToggleService.class);

        final EventBus eventBus = injector.getInstance(EventBus.class);
        final BindListener bindListener = new BindListener(standardModuleRepository, injector.getInstance(BindService.class), toggleService);

        eventBus.constructLinker(
                bindListener
        ).link(
                bindListener::listen,
                KeyPressEvent.ID
        );

        eventBus.subscribe(
                bindListener
        );


        toggleService.toggle(standardModuleRepository.getModuleContext(HudModule.class).supremacyModule(), true);
        toggleService.toggle(standardModuleRepository.getModuleContext(SprintModule.class).supremacyModule(), true);
        toggleService.toggle(standardModuleRepository.getModuleContext(ModuleListModule.class).supremacyModule(), true);

    }

    @Unique
    private GameTickEvent gameTickEvent;

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void tickHead(final CallbackInfo info) {
        final Provider<Injector> injectorProvider = InjectorProvider.getInstance();

        final Injector injector = injectorProvider.get();

        final EventBus eventBus = injector.getInstance(EventBus.class);

        this.gameTickEvent = new GameTickEvent();

        eventBus.publish(this.gameTickEvent);
    }

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void tickTail(final CallbackInfo info) {
        final Provider<Injector> injectorProvider = InjectorProvider.getInstance();

        final Injector injector = injectorProvider.get();

        final EventBus eventBus = injector.getInstance(EventBus.class);

        this.gameTickEvent = new GameTickEvent();
        this.gameTickEvent.setPost();

        eventBus.publish(this.gameTickEvent);
    }

}