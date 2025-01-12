package com.supremacy.screen.clickgui.pane;

import com.supremacy.animation.Animation;
import com.supremacy.animation.Easing;
import com.supremacy.module.CategoryEnum;
import com.supremacy.module.ModuleRepository;
import com.supremacy.module.type.render.ClickGuiModule;
import com.supremacy.nanovg.*;
import com.supremacy.screen.clickgui.data.ClickGuiData;
import com.supremacy.toggle.ToggleService;
import com.supremacy.util.ColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class CategoryPane implements Pane {

    private final ClickGuiData clickGuiData;

    private final NanoVGManager nanoVGManager;

    private CategoryEnum selectedCategory = CategoryEnum.COMBAT;
    private Color backgroundColor = new Color(20, 20, 20, 255);
    private Color textColor = new Color(60, 60, 60, 255);
    private Color hoveredTextColor = new Color(82, 80, 80, 255);
    private Color selectedTextColor = new Color(255, 255, 255, 255);
    private Color outlineColor = new Color(39, 39, 39, 255);
    private Color black = new Color(0, 0, 0, 255);

    private final FontRenderer fontRenderer = FontManager.getInstance().getFont("inter");

    private final FontRenderer iconFont = FontManager.getInstance().getFont("menu_font");

    // shit code i know
    private boolean init;
    private boolean close;

    private final ToggleService toggleService;

    private final ModuleRepository moduleRepository;

    private final ModuleScreen moduleScreen;

    public CategoryPane(ClickGuiData clickGuiData, ToggleService toggleService, ModuleRepository moduleRepository) {
        this.clickGuiData = clickGuiData;
        this.toggleService = toggleService;
        this.moduleRepository = moduleRepository;
        this.moduleScreen = new ModuleScreen(moduleRepository, toggleService, clickGuiData, this.clickGuiData.getAnimation());
        this.nanoVGManager = NanoVGManager.getInstance();
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        final float textX = this.clickGuiData.getX();

        if (this.init) {
            this.clickGuiData.getAnimation().run(255);
        }

        if (this.clickGuiData.getAnimation().getValue() >= 255) {
            this.init = false;
        }

        final int opacity = MathHelper.clamp((int) this.clickGuiData.getAnimation().getValue(), 0, 255);
        backgroundColor = ColorUtil.ensureOpacity(backgroundColor, opacity);
        textColor = ColorUtil.ensureOpacity(textColor, opacity);
        hoveredTextColor = ColorUtil.ensureOpacity(hoveredTextColor, opacity);
        selectedTextColor = ColorUtil.ensureOpacity(selectedTextColor, opacity);
        outlineColor = ColorUtil.ensureOpacity(outlineColor, opacity);
        black = ColorUtil.ensureOpacity(black, opacity);

        this.nanoVGManager.rect(this.clickGuiData.getX() + 42.5f, this.clickGuiData.getY() + 5, .5f, this.clickGuiData.getHeight() - 8, this.outlineColor.getRGB());
        this.nanoVGManager.rect(this.clickGuiData.getX() + 42f, this.clickGuiData.getY() + 5, .5f, this.clickGuiData.getHeight() - 8, this.black.getRGB());

        for (final CategoryEnum categoryEnum : CategoryEnum.values()) {


            final int baseY = (int) (this.clickGuiData.getY() + 42);
            int y = baseY + (categoryEnum.ordinal() * 36) - 37;
            boolean isHovered = false;

            final float textWidth = this.fontRenderer.getStringWidth(categoryEnum.getName(), 30); // Adjust with the actual font size used
            final float textHeight = this.fontRenderer.getStringHeight(categoryEnum.getName(), 30); // Adjust with the actual font size used

            //      COMBAT("Combat", "A"),
            //        MOVEMENT("Movement", "B"),
            //        PLAYER("Player", "F"),
            //        RENDER("Render", "C"),
            //        MISC("Misc", "D")

            // poop code
            String icon = "";
            switch (categoryEnum) {
                case COMBAT -> icon = "A";
                case MOVEMENT -> icon = "B";
                case PLAYER -> icon = "F";
                case RENDER -> icon = "C";
                case MISC -> icon = "D";
                case EXPLOIT -> icon = "G";
            }


            if (categoryEnum == selectedCategory) {
                final float fixedY = y + 3;

                this.nanoVGManager.roundedRect(
                        this.clickGuiData.getX() + 3,
                        fixedY,
                        40,
                        35,
                        0,
                        backgroundColor.getRGB());


                final NVGImage image = ImageManager.getInstance().getImage("skeetchainmail.png");

                // have to render multiple images because the first chainmail in module screen height and width is higher so the pic is smaller
                if (image != null && opacity > 35) {
//
//                    for (int i = 0; i < 8; i++) {
//                        for (int j = 0; j < 7; j++) {
//                            final float fixedX = this.clickGuiData.getX() + 3 + (i * 5);
//                            final float fixed = fixedY + (j * 5);
//                            image.drawImage(fixedX, fixed, 5, 5);
//                        }
//                    }

                    this.nanoVGManager.scissor(this.clickGuiData.getX() + 3, fixedY, 40, 35);

                    image.drawImage(this.clickGuiData.getX(), this.clickGuiData.getY() + 5, 1920 / 2, 1080 / 2);

                    this.nanoVGManager.resetScissor();
                }


                // top rect

                this.nanoVGManager.rect(this.clickGuiData.getX() + 2, fixedY - .5f, 40f, .2f, this.black.getRGB());
                this.nanoVGManager.rect(this.clickGuiData.getX() + 2, fixedY, 41f, .5f, this.outlineColor.getRGB());
                this.nanoVGManager.rect(this.clickGuiData.getX() + 2, fixedY + 35, 41f, .5f, this.outlineColor.getRGB());
                this.nanoVGManager.rect(this.clickGuiData.getX() + 2, fixedY + 35.5f, 40f, .2f, this.black.getRGB());


            }

            if (mouseX >= textX && mouseX <= (textX + 40) && mouseY >= y + 4 && mouseY <= ((y + 10) + 35)) {
                isHovered = true;
                clickGuiData.setCanDrag(false);
            }

            int finalTextColor = isHovered ? hoveredTextColor.getRGB() : textColor.getRGB();

            this.iconFont.drawCenteredString(icon, textX - this.iconFont.getStringWidth(icon, 30) + 22, y + 30, 30, categoryEnum == selectedCategory ? selectedTextColor.getRGB() : finalTextColor);
        }

        this.moduleScreen.setCategoryEnum(this.selectedCategory);
        this.moduleScreen.render(null, mouseX, mouseY, partialTicks);


        if (this.close && !init) {
            this.clickGuiData.getAnimation().run(0);
        }

        if (this.clickGuiData.getAnimation().getValue() <= 0 && close) {
            toggleService.toggle(this.moduleRepository.getModuleContext(ClickGuiModule.class).supremacyModule(), false);
            MinecraftClient.getInstance().setScreen(null);
            this.close = false;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (final CategoryEnum categoryEnum : CategoryEnum.values()) {
            final float baseY = this.clickGuiData.getY() + 42;
            float y = baseY + (categoryEnum.ordinal() * 36) - 37;
            boolean isHovered = false;

            final float textWidth = this.fontRenderer.getStringWidth(categoryEnum.getName(), 30); // Adjust with the actual font size used
            final float textHeight = this.fontRenderer.getStringHeight(categoryEnum.getName(), 30); // Adjust with the actual font size used

            final float textX = this.clickGuiData.getX();

            if (mouseX >= textX && mouseX <= (textX + 40) && mouseY >= y + 4 && mouseY <= ((y + 10) + 35)) {
                this.selectedCategory = categoryEnum;
            }
        }


        this.moduleScreen.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.moduleScreen.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double horizAmount, double verticalAmount) {
        this.moduleScreen.mouseScrolled(mouseX, mouseY, horizAmount, verticalAmount);
    }


    @Override
    public void init() {
        this.init = true;

        this.moduleScreen.initGui();
    }

    @Override
    public void close() {
        this.close = true;

        this.moduleScreen.onGuiClosed();
    }
}
