package com.supremacy.nanovg;


import com.supremacy.util.ColorUtil;
import net.minecraft.util.Formatting;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.system.MemoryUtil.memSlice;

public final class FontRenderer {

    private final NanoVGManager nvgManager = NanoVGManager.getInstance();
    private final long vg = NanoVGManager.getInstance().getVg();

    private final String name;

    private final ByteBuffer fontData;

    public FontRenderer(final String name, final InputStream inputStream) {
        this.name = name;
        this.fontData = ioResourceToByteBuffer(inputStream, 512 * 1024);
        nvgCreateFontMem(vg, this.name, this.fontData, false);
    }

    private static final String FORMATTING_CODES = "0123456789abcdefklmnori";
    private static final int[] FORMATTING_CODE_DATA = new int[16];

    private String filterCharacters(final String text) {
        final StringBuilder builder = new StringBuilder();
        boolean skip = false;

        for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);

            if (i != text.length()-1 && !skip && c == '§' && text.charAt(i + 1) == 'i') {
                builder.append("§i");
                skip = true;
                i += 1;
                continue;
            }

            if (skip || c == '§' || c >= 0x20 && c <= 0x7E) {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    static {
        for (int i = 0; i < 16; i++) {
            FORMATTING_CODE_DATA[i] = Formatting.values()[i].getColorValue();
        }
    }


    public void drawStringTest(String text, float x, float y, float size, int color, final boolean shadow, final int bloomAmount, final int alignment) {
        final boolean isRightAligned = (alignment & NVG_ALIGN_RIGHT) == NVG_ALIGN_RIGHT;
        final int initialColor = color;

        int alpha = (color >> 24) & 0xFF;

        y += 1;

        StringBuilder stringBuilder = new StringBuilder();
        boolean icon = false;


        text = filterCharacters(text);

        for (int i = 0; i < text.length(); i++) {
            final char character = text.charAt(i);
            if (character != '§') {
                stringBuilder.append(character);
                continue;
            }

            if (!stringBuilder.isEmpty()) {
                final String string = stringBuilder.toString();
                drawString(string, x, y, size, ColorUtil.applyOpacity(color, alpha / 255.f), shadow, bloomAmount, alignment);

                final float stringWidth = getStringWidth(string, size, alignment);
                if (isRightAligned) {
                    x -= stringWidth;
                } else {
                    x += stringWidth;
                }

                stringBuilder.setLength(0);
            }

            int codeIndex = i == text.length() - 1
                    ? 21
                    : FORMATTING_CODES.indexOf(text.charAt(i + 1));

            if (codeIndex < 16) {
                icon = false;


                if (codeIndex < 0) {
                    codeIndex = 15;
                }

                color = FORMATTING_CODE_DATA[codeIndex];
            } else {
                switch (codeIndex) {
                    // &l
                    case 17:
                        // weight = FontWeight.SEMI_BOLD;
                        break;
//                        case 18:
//                            strikethrough = true;
//                            break;
//                        case 19:
//                            underline = true;
//                            break;
//                        case 20:
//                            italic = true;
//                            break;
                    // &r
                    case 21:
                        //weight = FontWeight.REGULAR;
                        icon = false;
                        color = initialColor;
                        break;
                    // &i
                    case 22:
                        icon = true;
                        break;
                    default:
                        icon = true;
                        break;
                }
            }

            i++;
        }

        if (!stringBuilder.toString().trim().isEmpty()) {
            drawString(stringBuilder.toString(), x, y, size, ColorUtil.applyOpacity(color, alpha / 255.f), shadow, bloomAmount, alignment);
        }
    }

    public static ByteBuffer ioResourceToByteBuffer(final InputStream inputStream, final int bufferSize) {
        try {
            ByteBuffer buffer;

            if (inputStream != null) {
                try (ReadableByteChannel rbc = Channels.newChannel(inputStream)) {
                    buffer = createByteBuffer(bufferSize);

                    while (true) {
                        final int bytes = rbc.read(buffer);
                        if (bytes == -1) {
                            break;
                        }
                        if (buffer.remaining() == 0) {
                            buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("InputStream cannot be null");
            }

            buffer.flip();
            return memSlice(buffer);
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static ByteBuffer resizeBuffer(final ByteBuffer buffer, final int newCapacity) {
        final ByteBuffer newBuffer = createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    public void drawString(final String text, final float x, final float y, final float size, final int color) {
        drawString(text, x, y, size, color, false, 0, NVG_ALIGN_TOP | NVG_ALIGN_LEFT);
    }


    public void drawCenteredString(final String text, final float x, final float y, final float size, final int color) {
        drawString(text, x + getStringWidth(text, size) / 2f, y, size, color, false, 0, NVG_ALIGN_LEFT);
    }

    public void drawString(final String text, final float x, final float y, final float size, final int color, final boolean shadow) {
        drawString(text, x, y, size, color, shadow, 0, NVG_ALIGN_TOP | NVG_ALIGN_LEFT);
    }

    public void drawString(final String text, final float x, final float y, final float size, final int color, final boolean shadow, final int alignment) {
        drawString(text, x, y, size, color, shadow, 0, alignment);
    }

    public void drawString(String text, final float x, final float y, final float size, final int color, final boolean shadow, final int bloomAmount, final int alignment) {
        nvgFontFace(vg, name);
        nvgFontSize(vg, size);
        nvgTextAlign(vg, alignment);

        if (bloomAmount > 0) {
            int alpha = (color >> 24) & 0xFF;
            int bloomColor = (alpha << 24) | (color & 0x00FFFFFF);

            final NVGColor nvgBloomColor = nvgManager.createNVGColor(bloomColor);

            for (int i = 0; i <= 2; i++) {
                float blurRadius = i * bloomAmount;
                nvgFontBlur(vg, blurRadius);
                nvgFillColor(vg, nvgBloomColor);
                nvgText(vg, x, y, text);
            }

            nvgFontBlur(vg, 0.0f);

            nvgBloomColor.free();
        }

        if (shadow) {
            final NVGColor nvgShadowColor = nvgManager.createNVGColor((color & 0xFCFCFC) >> 2 | color & 0xFF000000);
            nvgFillColor(vg, nvgShadowColor);
            nvgText(vg, x + 0.5f, y + 0.5f, text);

            nvgShadowColor.free();
        }

        final NVGColor nvgTextColor = nvgManager.createNVGColor(color);
        nvgFillColor(vg, nvgTextColor);
        nvgText(vg, x, y, text);

        nvgTextColor.free();
    }

    public float getStringWidth(final String text, final float size, final int alignment) {
        nvgFontFace(vg, name);
        nvgFontSize(vg, size);
        nvgTextAlign(vg, alignment);

        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final FloatBuffer bounds = stack.mallocFloat(4);

            return nvgTextBounds(vg, 0, 0, text, bounds);
        }
    }

    public float getStringWidth(final String text, final float size) {
        return getStringWidth(text, size, NVG_ALIGN_TOP | NVG_ALIGN_LEFT);
    }

    public float getStringWidthTest(String text, final float size) {
        text = text.replaceAll("§[0-9a-zA-Z]", "");
        return getStringWidth(text, size, NVG_ALIGN_TOP | NVG_ALIGN_LEFT);
    }

    public float getStringHeight(final String text, final float size) {
        return getStringHeight(text, size, NVG_ALIGN_TOP | NVG_ALIGN_LEFT);
    }

    public float getStringHeight(final String text, final float size, final int alignment) {
        nvgFontFace(vg, name);
        nvgFontSize(vg, size);
        nvgTextAlign(vg, alignment);

        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final FloatBuffer bounds = stack.mallocFloat(4);

            nvgTextBounds(vg, 0, 0, text, bounds);

            return bounds.get(3) - bounds.get(1);
        }
    }

}
