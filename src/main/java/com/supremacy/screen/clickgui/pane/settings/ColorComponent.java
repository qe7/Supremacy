package com.supremacy.screen.clickgui.pane.settings;

import com.supremacy.module.ModuleRepository;
import com.supremacy.nanovg.FontManager;
import com.supremacy.nanovg.NanoVGManager;
import com.supremacy.screen.clickgui.pane.AbstractComponent;
import com.supremacy.screen.clickgui.pane.ModuleScreen;
import com.supremacy.setting.type.Setting;
import net.minecraft.util.math.MathHelper;
import java.awt.*;

public final class ColorComponent extends AbstractComponent {

    private final NanoVGManager nanoVGManager = NanoVGManager.getInstance();
    private final Setting<Color> value;

    private boolean extended = false;
    private boolean smallDragActive = false;
    private Color smallDragColor;
    private boolean largeDragActive = false;

    public ColorComponent(final ModuleRepository moduleRepository, ModuleScreen moduleScreen, Setting<?> abstractValue) {
        super(moduleRepository, moduleScreen, abstractValue);
        this.value = (Setting<Color>) abstractValue;
        this.smallDragColor = this.value.getValue();
    }

    @Override
    public void render(int mouseX, int mouseY) {
        int backgroundOpacity = MathHelper.clamp((int) (255 * this.getOpacity().getValue()), 0, 255);
        Color currentValue = this.value.getValue();

        FontManager.getInstance().getFont("verdana").drawString(this.value.getName(),
                (float) (this.x + 21f),
                (float) (this.y + (FontManager.getInstance().getFont("verdana").getStringHeight(value.getName(), 5f)) / 2f) - 1,
                5f,
                new Color(255, 255, 255, backgroundOpacity).getRGB());

        this.x = x + 30;

        // Draw color preview box
        this.nanoVGManager.rect(
                (float) this.x + this.frameWidth - 51.5f,
                (float) this.y + 1.5f,
                11, 5,
                new Color(0, 0, 0, backgroundOpacity).getRGB()
        );


        this.nanoVGManager.rect(
                (float) this.x + this.frameWidth - 51f,
                (float) this.y + 2,
                10, 4,
                new Color(currentValue.getRed(), currentValue.getGreen(), currentValue.getBlue(), backgroundOpacity).getRGB()
        );

        // Render color picker if extended
        if (this.extended) {
            this.nanoVGManager.rect(
                    (float) this.x + 27.5f,
                    (float) this.y + 7.5f,
                    68f, 58f,
                    new Color(0, 0, 0, backgroundOpacity).getRGB()
            );

            this.nanoVGManager.rect(
                    (float) this.x + 28,
                    (float) this.y + 8,
                    67, 57,
                    new Color(39, 39, 39, backgroundOpacity).getRGB()
            );

            // outline on color picker
            this.nanoVGManager.rect(
                    (float) this.x + 29.5f,
                    (float) this.y + 9.5f,
                    58, 53.5f,
                    new Color(0, 0, 0, backgroundOpacity).getRGB()
            );

            // outline on hue bar
            this.nanoVGManager.rect(
                    (float) this.x + 89f,
                    (float) this.y + 9.5f,
                    4.5f, 53.5f,
                    new Color(0, 0, 0, backgroundOpacity).getRGB()
            );

            renderColorPicker(mouseX, mouseY);
        }

        if (largeDragActive) {
            drawRect(mouseX, mouseY, 3, 3, new Color(255, 255, 255, 255).getRGB());
        }
    }


    private void renderColorPicker(int mouseX, int mouseY) {
        renderLargeColorPicker(mouseX, mouseY);
        renderHueBar(mouseX, mouseY);
    }

    private void renderLargeColorPicker(int mouseX, int mouseY) {
        for (int width = 0; width < 50; width++) {
            for (int height = 0; height < 45; height++) {
                float[] hsb = Color.RGBtoHSB(smallDragColor.getRed(), smallDragColor.getGreen(), smallDragColor.getBlue(), null);
                Color color = Color.getHSBColor(hsb[0], width / 50f, height / 45f);

                drawRect(getX() + width + 30, getY() + height + 10, 8, 8, color.getRGB());

                if (isHoveringCoords(getX() + width + 30, getY() + height + 10, 8, 8, mouseX, mouseY) && largeDragActive) {
                    this.value.setValue(color);
                }
            }
        }
    }

    private void renderHueBar(int mouseX, int mouseY) {
        for (int height = 0; height < 52; height++) {
            // Generate the hue color for the current height
            Color color = Color.getHSBColor(height / 52f, 1f, 1f);

            // Adjust rectangle dimensions and position for vertical layout
            drawRect(getX() + 90, getY() + 10 + height, 3, 1, color.getRGB());

            // Check for hover and update smallDragColor if dragging
            if (isHoveringCoords(getX() + 90, getY() + 10 + height, 3, 1, mouseX, mouseY)) {
                if (smallDragActive) {
                    smallDragColor = color;
                }
            }
        }
    }



    private void drawRect(double x, double y, double width, double height, int color) {
        this.nanoVGManager.rect((float) x, (float) y, (float) width, (float) height, color);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHoveringCoords(this.x + this.frameWidth - 51.5f, this.y + 1.5f, 12, 6, mouseX, mouseY)) {
            this.extended = !this.extended;
        }

        if (isHoveringCoords(getX() + 90, getY() + 11, 5, 52, mouseX, mouseY)) {
            smallDragActive = true;
        }

        if (isHoveringCoords(getX() + 30, getY() + 10, 55, 45, mouseX, mouseY)) {
            largeDragActive = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        smallDragActive = false;
        largeDragActive = false;
    }

    @Override
    public int getExtendValue() {
        return this.extended ? 70 : 10;
    }

    @Override
    public void initGui() {
        // Initialize any additional GUI elements here if needed
    }

    @Override
    public void onGuiClosed() {
        // Cleanup or reset operations when GUI is closed
    }
}
