package com.supremacy.util;

import java.awt.*;

public final class ColorUtil {

    public static int applyOpacity(final int color, final float opacity) {
        float clampedOpacity = Math.max(0.0f, Math.min(1.0f, opacity));

        final int alpha = (int) (clampedOpacity * 255.0f);

        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    public static Color ensureOpacity(final Color color, final float opacity) {
        if (color.getAlpha() != opacity) {
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (opacity));
        } else {
            return color;
        }
    }
}
