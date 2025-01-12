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
import com.supremacy.setting.type.collection.CollectionSetting;
import com.supremacy.setting.type.collection.ModuleMode;
import net.minecraft.util.math.MathHelper;

import java.awt.*;


public final class ModeComponent extends AbstractComponent {

    private final Setting<ModuleMode> enumValue;

    private final FontRenderer fontRenderer = FontManager.getInstance().getFont("verdana");

    private final NanoVGManager nanoVGManager = NanoVGManager.getInstance();

    private boolean extended;


    private float width;

    private int height = 0;

    private final Animation animation = new Animation(Easing.LINEAR, 200);

    private Color textColor = new Color(255, 255, 255);

    private Color secondTextColor = new Color(186, 186, 186);

    private Color separatorColor = new Color(0, 0, 0);

    private Color backgroundColor = new Color(31,31,31);

    private Color secondBackgroundColor = new Color(34, 34, 34);

    public ModeComponent(final ModuleRepository moduleRepository, ModuleScreen moduleButton, Setting<?> abstractValue) {
        super(moduleRepository, moduleButton, abstractValue);
        enumValue = (Setting<ModuleMode>) abstractValue;
    }

    @Override
    public void initGui() {

    }

    @Override
    public void render(int mouseX, int mouseY) {
        this.width = (this.getModuleScreen().getClickGuiContext().getWidth() / 2f) - 85;

        int backgroundOpacity = (int) (255 * this.getOpacity().getValue());
        backgroundOpacity = MathHelper.clamp(backgroundOpacity, 0, 255);

        if (textColor.getAlpha() != backgroundOpacity) {
            textColor = new Color(255, 255, 255, backgroundOpacity);
        }

        if (secondTextColor.getAlpha() != backgroundOpacity) {
            secondTextColor = new Color(186, 186, 186, backgroundOpacity);
        }

        fontRenderer.drawString(
                this.enumValue.getName(),
                (float) this.x + 21f,
                (float) (this.y + (6 - fontRenderer.getStringHeight(enumValue.getName(), 5f)) / 2f),
                5f,
                textColor.getRGB()
        );

        this.animation.run(height);

        final float animatedHeight = (float) this.animation.getValue();


        if (backgroundColor.getAlpha() != backgroundOpacity) {
            backgroundColor = new Color(31,31,31, backgroundOpacity);
        }

        if (secondBackgroundColor.getAlpha() != backgroundOpacity) {
            secondBackgroundColor = new Color(34, 34, 34, backgroundOpacity);
        }


        if (separatorColor.getAlpha() != backgroundOpacity) {
            separatorColor = new Color(27, 27, 27, backgroundOpacity);
        }

        nanoVGManager.rect((float) (this.x + 20f), (float) (y + 6.5f), width, 11, separatorColor.getRGB());

        nanoVGManager.rect((float) (this.x + 20.5f), (float) (y + 7), width - 1, 10, backgroundColor.getRGB());

        nanoVGManager.rect((float) (this.x + 20f), (float) (y + 20.5f), width, Math.max(-0.5f, animatedHeight + 1 - 5 ), animatedHeight > 1 ? separatorColor.getRGB() : Color.TRANSLUCENT);

        nanoVGManager.rect((float) (this.x + 20.5f), (float) (y + 21), width - 1, Math.max(-0.5f, animatedHeight ), animatedHeight > 1 ? secondBackgroundColor.getRGB() : Color.TRANSLUCENT);

        nanoVGManager.arrow((float) ((this.x + 12.5f) + this.width), (float) (y + 11), 3, secondTextColor.getRGB());



        if (secondTextColor.getAlpha() != backgroundOpacity) {
            secondTextColor = new Color(186, 186, 186, backgroundOpacity);
        }

        fontRenderer.drawString(this.enumValue.getValue().getName(),
                (float) (this.x + 23f),
                (float) (y + 7 + (10 - fontRenderer.getStringHeight(enumValue.getValue().getName(), 6)) / 2f) + 1,
                5,
                secondTextColor.getRGB()
        );

        final CollectionSetting<ModuleMode> collectionSetting = (CollectionSetting<ModuleMode>) this.enumValue;

        int i = 0;
        for (final ModuleMode moduleMode : collectionSetting.getCollection()) {
            if (this.animation.getValue() > (i+1) * 10 - 3) {

                boolean selected = this.enumValue.getValue() == moduleMode;

                fontRenderer.drawString(moduleMode.getName(),
                        (float) (this.x + 22f),
                        (float) (y + 23 + i * 10) + 0.5f,
                        5,
                        selected ? this.getGuiColor().getRGB() : textColor.getRGB()
                );
            }
            i++;
        }

        height = extended ? (collectionSetting.getCollection().size() * 10) + 5 : 0;
    }



    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.isHoveringCoords((this.x + 22f), y + 7, width, 10, mouseX, mouseY)) {
            this.extended = !this.extended;
        }

        if (this.isHoveringCoords((this.x + 22f), y + 20.5f, width - 30, height, mouseX, mouseY)) {
            final CollectionSetting<ModuleMode> collectionSetting = (CollectionSetting<ModuleMode>) this.enumValue;

            for (int i = 0; i < collectionSetting.getCollection().size(); i++) {
                final ModuleMode moduleMode = (ModuleMode) collectionSetting.getCollection().toArray()[i];

                //System.out.println(moduleMode.getName());

                if (this.isHoveringCoords((float) (this.x + 22f), (float) (y + 21 + i * 10), width - 6, 10, mouseX, mouseY)) {
                    this.enumValue.setValue(moduleMode);
                }
            }
        }
    }


    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    @Override
    public void onGuiClosed() {

    }

    @Override
    public int getExtendValue() {
        return (int) (20f + this.animation.getValue());
    }
}