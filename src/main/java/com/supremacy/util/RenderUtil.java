package com.supremacy.util;

import com.supremacy.nanovg.NanoVGManager;

import java.awt.*;

public final class RenderUtil {

    private static final NanoVGManager nanoVGManager = NanoVGManager.getInstance();

    public static void drawRect(final float x, final float y, final float width, final float height, final int color) {
        nanoVGManager.immediateRender(() -> {
            nanoVGManager.rect(x, y, 0.5f, height, color);
            nanoVGManager.rect(x + width, y, 0.5f, height, color);
            nanoVGManager.rect(x, y, width, 0.5f, color);
            nanoVGManager.rect(x, y + height - 0.5f, width, 0.5f, color);
        });
    }
}
