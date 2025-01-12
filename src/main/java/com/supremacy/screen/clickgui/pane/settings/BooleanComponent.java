package com.supremacy.screen.clickgui.pane.settings;

import com.supremacy.animation.Animation;
import com.supremacy.animation.Easing;
import com.supremacy.module.ModuleRepository;
import com.supremacy.nanovg.FontManager;
import com.supremacy.nanovg.FontRenderer;
import com.supremacy.nanovg.NanoVGManager;
import com.supremacy.screen.clickgui.pane.AbstractComponent;
import com.supremacy.screen.clickgui.pane.ModuleScreen;
import com.supremacy.setting.type.Setting;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public final class BooleanComponent extends AbstractComponent {

    private final Setting<Boolean> value;
    private final FontRenderer fontRenderer = FontManager.getInstance().getFont("verdana");
    private final NanoVGManager nanoVGManager = NanoVGManager.getInstance();
    private final Animation opacityAnimation = new Animation(Easing.LINEAR, 200);

    private Color textColor = new Color(255, 255, 255);

    private Color outlineColor = new Color(59, 59, 59);

    private Color black = new Color(0, 0, 0);

    private static Color GRADIENT_COLOR_SECOND = new Color(54, 52, 52);

    private static Color GRADIENT_COLOR_FIRST = new Color(36, 35, 35);



    public BooleanComponent(final ModuleRepository moduleRepository, ModuleScreen moduleScreen, Setting<?> abstractValue) {
        super(moduleRepository, moduleScreen, abstractValue);
        this.value = (Setting<Boolean>) abstractValue;
    }

    @Override
    public void initGui() {
        this.opacityAnimation.run(1);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        int backgroundOpacity = (int) (255 * this.getOpacity().getValue());
        backgroundOpacity = MathHelper.clamp(backgroundOpacity, 0, 255);

        if (textColor.getAlpha() != backgroundOpacity) {
            textColor = new Color(255, 255, 255, backgroundOpacity);
        }

        this.fontRenderer.drawString(this.value.getName(),
                (float) (this.x + 21f),
                (float) (this.y + (getExtendValue() - fontRenderer.getStringHeight(value.getName(), 5f)) / 2f) - 1,
                5f,
                textColor.getRGB());

        final float y = (float) (this.y + (getExtendValue() - 5) / 2f);

        if (outlineColor.getAlpha() != backgroundOpacity) {
            outlineColor = new Color(59, 59, 59, backgroundOpacity);
        }

        if (black.getAlpha() != backgroundOpacity) {
            black = new Color(0, 0, 0, backgroundOpacity);
        }

        this.opacityAnimation.run(this.value.getValue() ? 1 : 0);

        int opacity = (int) (255 * this.opacityAnimation.getValue());
        opacity = MathHelper.clamp(opacity, 0, 255);

        if (GRADIENT_COLOR_FIRST.getAlpha() != opacity) {
            GRADIENT_COLOR_FIRST = new Color(36, 35, 35, opacity);
        }


        this.nanoVGManager.rectOutline((float) (this.x + 8f), y, 3f, 3f, 1, black.getRGB());

        this.nanoVGManager.rect((float) (this.x + 8), y, 3, 3, GRADIENT_COLOR_SECOND.getRGB());

        final Color secondGradientColor = new Color(this.getGuiColor().getRed(), this.getGuiColor().getGreen(), this.getGuiColor().getBlue(), opacity);
        final Color secondGradientSecondColor = new Color(this.getGuiColor().darker().getRed(), this.getGuiColor().darker().getGreen(), this.getGuiColor().darker().getBlue(), opacity);

        this.nanoVGManager.drawHorizontalGradientRect((float) (this.x + 8), y, 3, 3, secondGradientColor.getRGB(), secondGradientSecondColor.getRGB());

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        final float y = (float) (this.y + (getExtendValue() - 7.5f) / 2f);
        if (isHoveringCoords((this.x + 8), y, 5, 5, mouseX, mouseY)) {
            this.value.setValue(!this.value.getValue());
            // Slide to the new value state (1.0 for true, 0.0 for false)
            this.opacityAnimation.run(1);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        // Handle mouse release events if needed
    }

    @Override
    public void onGuiClosed() {
        this.opacityAnimation.run(0);
    }

    @Override
    public int getExtendValue() {
        return 11;
    }
}
