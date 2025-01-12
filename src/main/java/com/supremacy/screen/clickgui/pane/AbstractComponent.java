package com.supremacy.screen.clickgui.pane;

import com.supremacy.animation.Animation;
import com.supremacy.module.ModuleRepository;
import com.supremacy.setting.type.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public abstract class AbstractComponent {
    protected final MinecraftClient minecraft = MinecraftClient.getInstance();

    private final ModuleScreen moduleScreen;
    private final Setting<?> abstractValue;
    private boolean extended = false;
    protected double x, y, extendValue;
    protected float frameWidth;

    private Animation opacity;

    private final ModuleRepository moduleRepository;

    public AbstractComponent(final ModuleRepository moduleRepository, final ModuleScreen moduleScreen, final Setting<?> abstractValue) {
        this.moduleScreen = moduleScreen;
        this.abstractValue = abstractValue;
        this.moduleRepository = moduleRepository;
    }

    public Color getGuiColor() {
        final int opacity = (int) MathHelper.clamp(255 * this.opacity.getValue(), 0, 255);

        return new Color(157, 200, 40, opacity);
    }

    public Animation getOpacity() {
        return opacity;
    }

    public void setOpacity(Animation opacity) {
        this.opacity = opacity;
    }

    public abstract void initGui();

    public abstract void render(int mouseX, int mouseY);

    public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);

    public abstract void mouseReleased(int mouseX, int mouseY, int state);

    public abstract void onGuiClosed();

    public ModuleScreen getModuleScreen() {
        return this.moduleScreen;
    }

    protected boolean isHoveringCoords(final double x, final double y, final double width, final double height, final int mouseX, final int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height - 0.5f;
    }

    public int getExtendValue() {
        return 0;
    }

    public Setting<?> getValue() {
        return this.abstractValue;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setFrameWidth(float frameWidth) {
        this.frameWidth = frameWidth;
    }

    public boolean isExtended() {
        return extended;
    }


    public void setExtended(boolean extended) {
        this.extended = extended;
    }
}
