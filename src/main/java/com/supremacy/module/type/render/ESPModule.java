package com.supremacy.module.type.render;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.supremacy.Globals;
import com.supremacy.event.core.EventBus;
import com.supremacy.event.events.type.render.RenderOverlayEvent;
import com.supremacy.module.CategoryEnum;
import com.supremacy.module.ModuleInfo;
import com.supremacy.module.SupremacyModule;
import com.supremacy.nanovg.NanoVGManager;
import com.supremacy.util.RenderUtil;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.AxisAngle4d;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.FloatBuffer;

@ModuleInfo(
        name = "ESP",
        description = "Renders a box around entities",
        category = CategoryEnum.RENDER
)
public final class ESPModule implements SupremacyModule {

    private final MinecraftClient minecraftClient;

    @Inject
    public ESPModule(final EventBus eventBus, MinecraftClient minecraftClient) {
        this.minecraftClient = minecraftClient;
        final NanoVGManager nanoVGManager = NanoVGManager.getInstance();

        eventBus.constructLinker(this).link(event -> {
            final RenderOverlayEvent renderOverlayEvent = (RenderOverlayEvent) event;

            if (minecraftClient.world == null || minecraftClient.player == null) {
                return;
            }

            for (Entity entity : minecraftClient.world.getEntities()) {
                if (!(entity instanceof LivingEntity)) {
                    continue;
                }

                if (!(this.minecraftClient.player.distanceTo(entity) <= 5.0f)) {
                    continue;
                }

                if (entity == minecraftClient.player) {
                    continue;
                }

                float[] screenPos = this.getBoundingBox(entity);

                if (screenPos == null) {
                    continue;
                }

                final float x = screenPos[0] - 6;
                final float y = screenPos[1] - 2;
                final float width = (screenPos[2] - screenPos[0]) + 12;
                final float height = (screenPos[3] - screenPos[1]) + 4;

                nanoVGManager.immediateRender(() -> {
                    RenderUtil.drawRect(x - 0.5f, y - 0.5f, width + 1, height + 1, Color.BLACK.getRGB());
                    RenderUtil.drawRect(x, y, width, height, Color.RED.getRGB());
                });
            }
        }, RenderOverlayEvent.ID);
    }

    private Vec3d projectToScreen(Vec3d worldPos) {
        Vec3d entityPos = new Vec3d(
                worldPos.x,
                worldPos.y,
                worldPos.z
        );

        Vec3d camPos = entityPos.subtract(minecraftClient.gameRenderer.getCamera().getPos());

        Vector4f pos = new Vector4f((float) camPos.x, (float) camPos.y, (float) camPos.z, 1.0f).mul(Globals.worldMatrix).mul(Globals.projectionMatrix);

        if (pos.w() <= 0.0f) {
            return null;
        }

        float invW = 1.0f / pos.w() * 0.5f;
        float screenX = (pos.x() * invW + 0.5f) * this.minecraftClient.getWindow().getScaledWidth();
        float screenY = (0.5f - pos.y() * invW) * this.minecraftClient.getWindow().getScaledHeight();

        return new Vec3d(screenX, screenY, 0);
    }

    private float[] getBoundingBox(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return null;
        }

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    final double x1 = x == 0 ? entity.getBoundingBox().maxX : entity.getBoundingBox().minX;
                    final double y1 = y == 0 ? entity.getBoundingBox().maxY : entity.getBoundingBox().minY;
                    final double z1 = z == 0 ? entity.getBoundingBox().maxZ : entity.getBoundingBox().minZ;

                    Vec3d entityPos = new Vec3d(
                            x1,
                            y1,
                            z1
                    );

                    Vec3d camPos = entityPos.subtract(minecraftClient.gameRenderer.getCamera().getPos());

                    Vector4f pos = new Vector4f((float) camPos.x, (float) camPos.y, (float) camPos.z, 1.0f).mul(Globals.worldMatrix).mul(Globals.projectionMatrix);

                    if (pos.w() <= 0.0f) {
                        return null;
                    }

                    float invW = 1.0f / pos.w() * 0.5f;
                    float screenX = (pos.x() * invW + 0.5f) * this.minecraftClient.getWindow().getScaledWidth();
                    float screenY = (0.5f - pos.y() * invW) * this.minecraftClient.getWindow().getScaledHeight();

                    minX = Math.min(minX, screenX);
                    minY = Math.min(minY, screenY);
                    maxX = Math.max(maxX, screenX);
                    maxY = Math.max(maxY, screenY);
                }
            }
        }

        if (minX != Double.MAX_VALUE && minY != Double.MAX_VALUE && maxX != Double.MIN_VALUE && maxY != Double.MIN_VALUE) {
            return new float[]{(float) minX, (float) minY, (float) maxX, (float) maxY};
        }

        return null;
    }
}