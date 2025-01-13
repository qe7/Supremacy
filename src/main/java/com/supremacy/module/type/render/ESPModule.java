package com.supremacy.module.type.render;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.supremacy.Globals;
import com.supremacy.event.core.EventBus;
import com.supremacy.event.events.type.render.RenderOverlayEvent;
import com.supremacy.module.CategoryEnum;
import com.supremacy.module.ModuleInfo;
import com.supremacy.module.SupremacyModule;
import com.supremacy.nanovg.FontManager;
import com.supremacy.nanovg.NanoVGManager;
import com.supremacy.setting.linker.SettingLinkerFactory;
import com.supremacy.setting.registrar.SettingRegistrar;
import com.supremacy.setting.type.Setting;
import com.supremacy.util.RenderUtil;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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

    private final Setting<Boolean> box;
    private final Setting<Boolean> boxFill;
    private final Setting<Boolean> name;
    private final Setting<Boolean> healthBar;
    private final Setting<Boolean> armorBar;
    private final Setting<Boolean> healthDigit;
    private final Setting<Boolean> armorDigit;

    private final Setting<Boolean> outline;
    private final Setting<Boolean> background;

    @Inject
    public ESPModule(final EventBus eventBus, MinecraftClient minecraftClient, final SettingRegistrar settingRegistrar) {
        this.minecraftClient = minecraftClient;
        final NanoVGManager nanoVGManager = NanoVGManager.getInstance();

        this.box = settingRegistrar.registerSetting("Box", true);
        this.boxFill = settingRegistrar.registerSetting("Box Fill", true);
        this.name = settingRegistrar.registerSetting("Name", true);
        this.healthBar = settingRegistrar.registerSetting("Health Bar", true);
        this.armorBar = settingRegistrar.registerSetting("Armor Bar", true);
        this.healthDigit = settingRegistrar.registerSetting("Health", true);
        this.armorDigit = settingRegistrar.registerSetting("Armor", true);

        this.outline = settingRegistrar.registerSetting("Outline", true);
        this.background = settingRegistrar.registerSetting("Background", true);

        eventBus.constructLinker(this).link(_ -> {
            if (minecraftClient.world == null || minecraftClient.player == null) {
                return;
            }

            for (Entity entity : minecraftClient.world.getEntities()) {
                if (!(entity instanceof PlayerEntity)) {
                    continue;
                }

                if (entity == minecraftClient.player) {
                    continue;
                }

                float[] screenPos = this.getBoundingBox(entity);

                if (screenPos == null) {
                    continue;
                }

                final float x = screenPos[0];
                final float y = screenPos[1];
                final float width = (screenPos[2] - screenPos[0]);
                final float height = (screenPos[3] - screenPos[1]);

                nanoVGManager.immediateRender(() -> {
                    if (this.boxFill.getValue()) {
                        nanoVGManager.rect(x, y, width, height, new Color(0, 0, 0, 50).getRGB());
                    }

                    if (this.box.getValue()) {
                        if (this.outline.getValue()) {
                            RenderUtil.drawRect(x - 0.5f, y - 0.5f, width + 1, height + 1, Color.BLACK.getRGB());
                            RenderUtil.drawRect(x + 0.5f, y + 0.5f, width - 1, height - 1, Color.BLACK.getRGB());
                        }
                        RenderUtil.drawRect(x, y, width, height, Color.WHITE.getRGB());
                    }

                    if (this.name.getValue()) {
                        final float nameWidth = FontManager.getInstance().getFont("verdanab").getStringWidth(entity.getName().getString(), 7f);
                        final float nameHeight = FontManager.getInstance().getFont("verdanab").getStringHeight(entity.getName().getString(), 7f);
                        final float nameX = x + (width / 2) - (nameWidth / 2);

                        if (this.background.getValue()) {
                            nanoVGManager.rect(nameX - 2, y - 4 - nameHeight, nameWidth + 4, nameHeight + 2, new Color(0, 0, 0, 50).getRGB());
                        }

                        FontManager.getInstance().getFont("verdanab").drawString(
                                entity.getName().getString(),
                                nameX,
                                y - 3 - FontManager.getInstance().getFont("verdanab").getStringHeight(entity.getName().getString(), 7f),
                                7f,
                                Color.WHITE.getRGB(),
                                true
                        );
                    }

                    if (this.healthBar.getValue()) {
                        final float healthWidth = 0.5f;
                        final float healthX = x - 3;
                        final float healthHeight = height * ((LivingEntity) entity).getHealth() / ((LivingEntity) entity).getMaxHealth();

                        if (this.outline.getValue()) {
                            nanoVGManager.rect(healthX - 0.5f, y - 0.5f, healthWidth + 1, height + 1, Color.BLACK.getRGB());
                        }

                        if (((LivingEntity) entity).getHealth() > 0) {
                            nanoVGManager.rect(healthX, y + height - healthHeight, healthWidth, healthHeight, Color.GREEN.getRGB());
                        }

                        if (this.healthDigit.getValue() && ((LivingEntity) entity).getHealth() != 0) {
                            final float healthDigitWidth = FontManager.getInstance().getFont("verdana").getStringWidth(String.valueOf((int) ((LivingEntity) entity).getHealth()), 5f);
                            final float healthDigitX = healthX - healthDigitWidth - 1f;
                            final float healthDigitY = y + height - healthHeight;

                            FontManager.getInstance().getFont("verdana").drawString(String.valueOf((int) ((LivingEntity) entity).getHealth()), healthDigitX, healthDigitY, 5f, Color.WHITE.getRGB());
                        }
                    }

                    if (this.armorBar.getValue()) {
                        // draw armor bar at bottom of box
                        final float armorWidth = width * ((LivingEntity) entity).getArmor() / 20;

                        if (this.outline.getValue()) {
                            nanoVGManager.rect(x - 0.5f, y + height + 2.5f, width + 1, 1, Color.BLACK.getRGB());
                        }

                        if (((LivingEntity) entity).getArmor() > 0) {
                            nanoVGManager.rect(x, y + height + 3f, armorWidth, 0.5f, Color.CYAN.getRGB());
                        }

                        if (this.armorDigit.getValue() && ((LivingEntity) entity).getArmor() > 0) {
                            final float armorDigitWidth = FontManager.getInstance().getFont("verdana").getStringWidth(String.valueOf(((LivingEntity) entity).getArmor()), 5f);
                            final float armorDigitX = x - armorDigitWidth;
                            final float armorDigitY = y + height + 4.5f;

                            FontManager.getInstance().getFont("verdana").drawString(String.valueOf(((LivingEntity) entity).getArmor()), armorDigitX, armorDigitY, 5f, Color.WHITE.getRGB());
                        }
                    }
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