package com.supremacy.event.events.type.game;

import com.supremacy.event.events.AbstractPrePostEvent;

public class PlayerPositionUpdateEvent extends AbstractPrePostEvent {
    public static final int ID = 2;

    private float yaw;
    private float pitch;
    private final float prevYaw;
    private final float prevPitch;

    private double posX, posY, posZ;
    private boolean onGround, rotated;

    public PlayerPositionUpdateEvent(float yaw, float pitch, float prevYaw, float prevPitch, double posX, double posY, double posZ, boolean ground) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.prevYaw = prevYaw;
        this.prevPitch = prevPitch;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.onGround = ground;
    }

    public void setPitch(float pitch) {
        if (!rotated) {
            this.rotated = this.pitch - pitch != 0.0F;
        }
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        if (!rotated) {
            this.rotated = this.yaw - yaw != 0.0F;
        }
        this.yaw = yaw;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getPrevYaw() {
        return prevYaw;
    }

    public float getPrevPitch() {
        return prevPitch;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isRotated() {
        return rotated;
    }

    public void setRotated(boolean rotated) {
        this.rotated = rotated;
    }

    @Override
    public int id() {
        return ID;
    }
}
