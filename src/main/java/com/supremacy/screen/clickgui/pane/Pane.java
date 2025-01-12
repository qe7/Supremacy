package com.supremacy.screen.clickgui.pane;

public interface Pane {
    void draw(int mouseX, int mouseY, float partialTicks);

    void mouseClicked(int mouseX, int mouseY, int mouseButton);

    void mouseReleased(int mouseX, int mouseY, int state);

    void mouseScrolled(double mouseX, double mouseY, double horizAmount, double verticalAmount);

    void init();

    void close();
}
