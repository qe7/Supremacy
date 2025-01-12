package com.supremacy.event.events.type.render;

import com.supremacy.event.events.Event;
import net.minecraft.client.gui.DrawContext;

public record RenderOverlayEvent(DrawContext drawContext) implements Event {
    public static final int ID = 1;

    @Override
    public int id() {
        return ID;
    }
}
