package com.supremacy.module.type.movement;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.supremacy.event.core.EventBus;
import com.supremacy.event.events.type.game.PlayerPositionUpdateEvent;
import com.supremacy.module.CategoryEnum;
import com.supremacy.module.ModuleInfo;
import com.supremacy.module.SupremacyModule;
import com.supremacy.provider.suffix.SuffixProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

@Singleton
@ModuleInfo(
        name = "Sprint",
        description = "Automatically sprint",
        category = CategoryEnum.MOVEMENT
)
public final class SprintModule implements SupremacyModule {

    @Inject
    public SprintModule(final EventBus eventBus, final MinecraftClient minecraftClient) {
        eventBus.constructLinker(this).link(_ -> {
            final ClientPlayerEntity player = minecraftClient.player;

            if (player == null) {
                return;
            }

            if (player.forwardSpeed <= 0) {
                return;
            }

            player.setSprinting(true);
        }, PlayerPositionUpdateEvent.ID);
    }
}
