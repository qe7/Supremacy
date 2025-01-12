package com.supremacy.util;

public final class MathUtil {

    public static double random(final double min, final double max, float interval) {
        final double normalized = (max - min) + min;
        final double random = Math.random() * normalized;

        return Math.round(random / interval) * interval;
    }
}
