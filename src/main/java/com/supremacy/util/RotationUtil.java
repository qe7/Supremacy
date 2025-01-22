package com.supremacy.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public final class RotationUtil {

    /**
     * Apply the game's mouse sensitivity to the yaw and pitch angles
     *
     * @param rotationVector The yaw and pitch angles to apply the game's mouse sensitivity to
     * @return A Vector containing the yaw and pitch angles with the game's mouse sensitivity applied
     */
    public static float[] applyGCD(final float[] rotationVector, float[] previousRotation) {
        final double mouseSensitivity = MinecraftClient.getInstance().options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
        final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;

        final float yaw = previousRotation[0] + (float) (Math.round((rotationVector[0] - previousRotation[0]) / multiplier) * multiplier);
        final float pitch = MathHelper.clamp(previousRotation[1] + (float) (Math.round((rotationVector[1] - previousRotation[1]) / multiplier) * multiplier), -90, 90);

        return new float[]{yaw, pitch};
    }

    /**
     * Apply sanity checks to the yaw and pitch angles
     *
     * @param rotationVector The yaw and pitch angles to apply sanity checks to
     * @return A Vector containing the yaw and pitch angles with sanity checks applied
     */
    public static float[] applySanity(final float[] rotationVector) {
        final float yaw = rotationVector[0];
        final float pitch = MathHelper.clamp(rotationVector[1], -90, 90);

        return new float[]{yaw, pitch};
    }

    /**
     * Calculate the yaw and pitch angles required to look at an entity
     *
     * @param entity The entity to look at
     * @return A Float array containing the yaw and pitch angles required to look at the entity
     */
    public static float[] getRotations(final Entity entity) {
        // Calculate the difference in the x, y, and z coordinates between the player and the entity
        final double deltaX = entity.getX() + (entity.getX() - entity.lastRenderX) - MinecraftClient.getInstance().player.getX();
        final double deltaY = entity.getY() + (entity.getEyeHeight(entity.getPose())) - (MinecraftClient.getInstance().player.getY() + MinecraftClient.getInstance().player.getEyeHeight(MinecraftClient.getInstance().player.getPose()));
        final double deltaZ = entity.getZ() + (entity.getZ() - entity.lastRenderZ) - MinecraftClient.getInstance().player.getZ();

        // Calculate the Euclidean distance in the xz-plane between the player and the entity
        final double distance = Math.sqrt(deltaX*deltaX + deltaZ*deltaZ);

        // Calculate the yaw angle in spherical coordinates
        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ));
        // Calculate the pitch angle in spherical coordinates
        final float pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance));

        // Calculate the adjustment angle based on the quadrant of the entity relative to the player
        final double quadrantAdjustment = Math.toDegrees(Math.atan(deltaZ / deltaX));
        // Adjust the yaw angle based on the quadrant of the entity relative to the player
        if (deltaX < 0 && deltaZ < 0) {
            yaw = (float) (90 + quadrantAdjustment);
        } else if (deltaX > 0 && deltaZ < 0) {
            yaw = (float) (-90 + quadrantAdjustment);
        }

        // Return the calculated yaw and pitch angles
        return new float[]{yaw, pitch};
    }

}
