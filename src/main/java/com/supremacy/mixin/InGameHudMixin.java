package com.supremacy.mixin;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.supremacy.event.core.EventBus;
import com.supremacy.event.events.type.render.RenderOverlayEvent;
import com.supremacy.provider.provider.InjectorProvider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Unique
    private final Provider<Injector> injectorProvider = InjectorProvider.getInstance();


    @Inject(method = "render", at = @At("HEAD"))
    private void renderMixin(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        final Injector injector = injectorProvider.get();

        final EventBus eventBus = injector.getInstance(EventBus.class);

        eventBus.publish(new RenderOverlayEvent(context));
    }
}