package com.supremacy.screen.clickgui.data;

import com.supremacy.animation.Animation;
import com.supremacy.animation.Easing;

public class ClickGuiData {

    private float x, y, width, height;
    private float oldX, oldY;

    private boolean canDrag;
    private final Animation animation = new Animation(Easing.EASE_IN_OUT_CUBIC, 200);

    public ClickGuiData(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getOldX() {
        return oldX;
    }

    public void setOldX(float oldX) {
        this.oldX = oldX;
    }

    public float getOldY() {
        return oldY;
    }

    public void setOldY(float oldY) {
        this.oldY = oldY;
    }

    public boolean isCanDrag() {
        return canDrag;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setCanDrag(boolean canDrag) {
        this.canDrag = canDrag;
    }
}
