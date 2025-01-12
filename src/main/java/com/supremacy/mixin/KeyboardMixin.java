package com.supremacy.mixin;

import com.google.inject.Injector;
import com.supremacy.event.core.EventBus;
import com.supremacy.event.events.type.key.KeyPressEvent;
import com.supremacy.provider.provider.InjectorProvider;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public final class KeyboardMixin {

    @Inject(method = "onKey", at = @At(value = "HEAD", target = "Lnet/minecraft/client/Keyboard;onKey(JIIII)V"))
    public void keyUpdatePre(final long window, final int key, final int scancode, final int action, final int modifiers, final CallbackInfo callbackInfo) {
        if (window != MinecraftClient.getInstance().getWindow().getHandle()) {
            return;
        }

        final Injector injector = InjectorProvider.getInstance().get();

        final EventBus eventBus = injector.getInstance(EventBus.class);


        eventBus.publish(new KeyPressEvent(key));

    }


}