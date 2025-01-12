package com.supremacy.nanovg;

import org.lwjgl.nanovg.NVGPaint;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.system.MemoryUtil.memSlice;

public final class NVGImage {

    private final NanoVGManager nvgManager = NanoVGManager.getInstance();
    private final long vg = NanoVGManager.getInstance().getVg();
    private final ByteBuffer imageData;
    private final int imageHandle;

    public NVGImage(final InputStream inputStream, final int flags) {
        this.imageData = this.ioResourceToByteBuffer(inputStream, 512 * 1024);
        this.imageHandle = nvgCreateImageMem(vg, flags, this.imageData);
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

    public NVGImage(final InputStream inputStream) {
        this(inputStream, 0);
    }

    public void drawImage(final float x, final float y, final float width, final float height) {
        final NVGPaint nvgPaint = nvgManager.createNVGPaint();

        nvgImagePattern(
                vg,
                x,
                y,
                width,
                height,
                0,
                imageHandle,
                1,
                nvgPaint
        );

        nvgBeginPath(vg);
        nvgRect(
                vg,
                x,
                y,
                width,
                height
        );
        nvgFillColor(vg, NanoVGManager.getInstance().createNVGColor(Color.BLACK.getRGB())); // Red color
        nvgImagePattern(
                vg,
                x,
                y,
                width,
                height,
                0,
                imageHandle,
                1,
                nvgPaint
        );

        nvgFillPaint(vg, nvgPaint);
        nvgFill(vg);
        nvgClosePath(vg);

        nvgPaint.free();
    }

}