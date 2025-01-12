package com.supremacy.processor.entity;

import com.google.inject.Inject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.Iterator;
import java.util.List;

public final class EntityProcessor {

    private final MinecraftClient minecraftClient;

    @Inject
    public EntityProcessor(MinecraftClient minecraftClient) {
        this.minecraftClient = minecraftClient;
    }

    public Entity process(final Iterable<Entity> entities, final double range) {

        for (final Entity entity : entities) {
            if (entity.isAlive() && entity != this.minecraftClient.player && !entity.isInvisible() && this.minecraftClient.player.distanceTo(entity) <= range && entity instanceof LivingEntity) {
                return entity;
            }
        }
        return null;
    }

}
