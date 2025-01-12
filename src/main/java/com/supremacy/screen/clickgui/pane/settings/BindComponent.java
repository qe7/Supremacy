package com.supremacy.screen.clickgui.pane.settings;

import com.supremacy.bind.BindService;
import com.supremacy.bind.StandardBindService;
import com.supremacy.module.ModuleContext;
import com.supremacy.module.ModuleRepository;
import com.supremacy.nanovg.FontManager;
import com.supremacy.nanovg.FontRenderer;
import com.supremacy.nanovg.NanoVGManager;
import com.supremacy.provider.provider.InjectorProvider;
import com.supremacy.screen.clickgui.pane.AbstractComponent;
import com.supremacy.screen.clickgui.pane.ModuleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public final class BindComponent extends AbstractComponent {

    private final FontRenderer fontRenderer = FontManager.getInstance().getFont("verdana");
    private final NanoVGManager nanoVGManager = NanoVGManager.getInstance();

    private final ModuleContext moduleData;

    private boolean binding;
    private int height = 10;

    private Color textColor = new Color(186, 186, 186);

    private final BindService bindService;

    public BindComponent(final ModuleRepository moduleRepository, ModuleScreen moduleScreen, ModuleContext moduleData) {
        super(moduleRepository, moduleScreen, null);
        this.moduleData = moduleData;
        this.bindService = InjectorProvider.getInstance().get().getInstance(BindService.class);
    }

    @Override
    public void initGui() {

    }

    @Override
    public void render(int mouseX, int mouseY) {
        if (this.getOpacity() == null) {
            return;
        }
        int backgroundOpacity = (int) (255 * this.getOpacity().getValue());
        backgroundOpacity = MathHelper.clamp(backgroundOpacity, 0, 255);
        final float y = (float) (this.y + (getExtendValue() - height) / 2f) - 13;

        final String text = binding ?
                "[?]" : this.bindService.getKey(moduleData.supremacyModule()) == -1 ?
                    "[-]" : "[" + InputUtil.fromKeyCode(this.bindService.getKey(moduleData.supremacyModule()), 0).getLocalizedText().getString() + "]";

        if (textColor.getAlpha() != backgroundOpacity) {
            textColor = new Color(186, 186, 186, backgroundOpacity);
        }

        this.fontRenderer.drawString(
                text,
                (float) (x + frameWidth - fontRenderer.getStringWidth(text, 5)) - 5,
                y + 6.5f + (height - fontRenderer.getStringHeight(text, 5)) / 2f,
                5,
                textColor.getRGB()
        );

        if (binding) {
         //   getModuleScreen().getClickGuiContext().setCanClose(false);
            for (int key = GLFW.GLFW_KEY_SPACE; key <= GLFW.GLFW_KEY_MENU; key++) {
                if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), key)) {
                    if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_BACKSPACE) {
                        this.bindService.unbind(moduleData.supremacyModule());
                        binding = false;
//                        getModuleScreen().getClickGuiContext().setCanClose(true);
                        return;
                    }

                    this.bindService.bind(this.moduleData.supremacyModule(), key);
//                    getModuleScreen().getClickGuiContext().setCanClose(true);
                    binding = false;
                }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (binding) {
            binding = false;
        }

        final String text = binding ?
                "[?]" : this.bindService.getKey(moduleData.supremacyModule()) == -1 ?
                "[...]" : "[" + InputUtil.fromKeyCode(this.bindService.getKey(moduleData.supremacyModule()), 0).getLocalizedText().getString() + "]";

        final float y = (float) (this.y + (getExtendValue() - height) / 2f) - 13;
        if (this.isHoveringCoords(
                (float) (x + frameWidth - fontRenderer.getStringWidth(text, 6.5f)) - 5,
                y + 6.5f + (height - fontRenderer.getStringHeight(text, 6.5f)) / 2f,
                fontRenderer.getStringWidth(text, 6.5f), height - 2, mouseX, mouseY)) {
            binding = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    @Override
    public void onGuiClosed() {
        binding = false;
    }

    @Override
    public int getExtendValue() {
        return 0;
    }
}
