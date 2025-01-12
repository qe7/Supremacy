package com.supremacy.screen.clickgui;

import com.supremacy.module.ModuleRepository;
import com.supremacy.screen.clickgui.data.ClickGuiData;
import com.supremacy.screen.clickgui.pane.MainPane;
import com.supremacy.screen.clickgui.pane.Pane;
import com.supremacy.toggle.ToggleService;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class ClickGuiScreen extends Screen {

    private final ClickGuiData clickGuiContext = new ClickGuiData(200, 100, 330, 350);
    private final Pane pane;

    private boolean isResizing = false;
    private boolean dragging = false;
    private float oldX;
    private float oldY;

    public ClickGuiScreen(final ToggleService toggleService, final ModuleRepository moduleRepository) {
        super(Text.of("supremacy_click_gui"));
        this.pane = new MainPane(clickGuiContext, toggleService, moduleRepository);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (this.isResizing) {
            float newWidth = mouseX - this.clickGuiContext.getX() + 2;
            float newHeight = mouseY - this.clickGuiContext.getY() + 1;

            this.clickGuiContext.setWidth(Math.max(newWidth, 250));
            this.clickGuiContext.setHeight(Math.max(newHeight, 250));
        }

        if (this.dragging) {
            this.clickGuiContext.setX(oldX + mouseX);
            this.clickGuiContext.setY(oldY + mouseY);
        }

        this.clickGuiContext.setCanDrag(true);

        this.pane.draw(mouseX, mouseY, delta);
    }

    private boolean isMouseOverBottomRightCorner(final double mouseX, final double mouseY) {
        final int padding = 10;
        final int rectWidth = 10;
        final int rectHeight = 10;

        final float cornerXStart = (this.clickGuiContext.getX() + this.clickGuiContext.getWidth() - rectWidth - padding) + 10;
        final float cornerYStart = this.clickGuiContext.getY() + this.clickGuiContext.getHeight() - rectHeight - padding + 10;
        final float cornerXEnd = (cornerXStart + rectWidth);
        final float cornerYEnd = cornerYStart + rectHeight;



        return mouseX >= cornerXStart && mouseX <= cornerXEnd && mouseY >= cornerYStart && mouseY <= cornerYEnd;
    }

    private boolean isMouseHoveringPanel(final int mouseX, final int mouseY) {
        return mouseX >= this.clickGuiContext.getX() && mouseX <= this.clickGuiContext.getX() + this.clickGuiContext.getWidth() &&
                mouseY >= this.clickGuiContext.getY() && mouseY <= this.clickGuiContext.getY() + this.clickGuiContext.getHeight();
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.isMouseOverBottomRightCorner(mouseX, mouseY)) {
            this.clickGuiContext.setCanDrag(false);
            isResizing = true;
        }

        if (button == 0 && this.isMouseHoveringPanel((int) mouseX, (int) mouseY)) {
            if (this.clickGuiContext.isCanDrag())
                this.dragging = true;
            oldX = this.clickGuiContext.getX() - (int) mouseX;
            oldY = this.clickGuiContext.getY() - (int) mouseY;
        }

        this.pane.mouseClicked((int) mouseX, (int) mouseY, button);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.isResizing = false;
        this.dragging = false;

        this.pane.mouseReleased((int) mouseX, (int) mouseY, button);

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizAmount, double verticalAmount) {
        this.pane.mouseScrolled((int) mouseX, (int) mouseY, horizAmount, verticalAmount);

        return super.mouseScrolled(mouseX, mouseY, horizAmount, verticalAmount);
    }

    @Override
    public void close() {
        this.pane.close();
    }

    @Override
    public void init() {
        this.pane.init();
    }

}
