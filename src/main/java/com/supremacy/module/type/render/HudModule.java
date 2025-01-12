package com.supremacy.module.type.render;

import com.google.inject.Inject;
import com.supremacy.event.core.EventBus;
import com.supremacy.event.events.type.render.RenderOverlayEvent;
import com.supremacy.module.CategoryEnum;
import com.supremacy.module.ModuleInfo;
import com.supremacy.module.SupremacyModule;
import net.minecraft.client.MinecraftClient;

@ModuleInfo(
        name = "Hud",
        description = "A heads up display.",
        category = CategoryEnum.RENDER
)
public final class HudModule implements SupremacyModule {

    @Inject
    public HudModule(final EventBus eventBus, final MinecraftClient minecraftClient) {
        eventBus.constructLinker(this).link(event -> {
            final RenderOverlayEvent renderOverlayEvent = (RenderOverlayEvent) event;
            final String formatted = String.format("§cS§rupremacy §7(§r%s FPS§7)", minecraftClient.getCurrentFps());
            renderOverlayEvent.drawContext().drawText(minecraftClient.textRenderer, formatted, 2, 2, -1, true);
        }, RenderOverlayEvent.ID);
    }

}
