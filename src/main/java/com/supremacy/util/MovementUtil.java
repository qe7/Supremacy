package com.supremacy.util;

import net.minecraft.client.MinecraftClient;

public final class MovementUtil {

    public static boolean isMoving() {
        if (MinecraftClient.getInstance().player == null) {
            return false;
        }
        return !(MinecraftClient.getInstance().player.getVelocity().x == 0 && MinecraftClient.getInstance().player.getVelocity().z == 0);
    }

    public static void setSpeed(double moveSpeed) {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        setSpeed(moveSpeed, MinecraftClient.getInstance().player.getYaw(), MinecraftClient.getInstance().player.input.movementSideways, MinecraftClient.getInstance().player.input.movementForward);
    }

    public static void setSpeed(double moveSpeed, float yaw, double strafe, double forward) {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }

        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += ((forward > 0.0D) ? -45 : 45);
            } else if (strafe < 0.0D) {
                yaw += ((forward > 0.0D) ? 45 : -45);
            }

            strafe = 0.0D;
            if (forward > 0.0D) {
                forward = 1.0D;
            } else if (forward < 0.0D) {
                forward = -1.0D;
            }
        }

        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }

        double motionX = Math.cos(Math.toRadians((yaw + 90.0F)));
        double motionZ = Math.sin(Math.toRadians((yaw + 90.0F)));

        double velocityX = forward * moveSpeed * motionX + strafe * moveSpeed * motionZ;
        double velocityZ = forward * moveSpeed * motionZ - strafe * moveSpeed * motionX;

        MinecraftClient.getInstance().player.setVelocity(velocityX, MinecraftClient.getInstance().player.getVelocity().y, velocityZ);
    }
}
