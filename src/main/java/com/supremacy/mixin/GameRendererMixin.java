package com.supremacy.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.supremacy.Globals;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {


    @Inject(method = "renderWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0))
    private void onRenderWorld(RenderTickCounter tickCounter, CallbackInfo ci, @Local MatrixStack matrixStack, @Local(ordinal = 0) Matrix4f ma1, @Local(ordinal = 1) Matrix4f ma2) {
        var newMatStack = new MatrixStack();
        newMatStack.multiplyPositionMatrix(ma2);

        Globals.worldMatrix = newMatStack.peek().getPositionMatrix();
        Globals.projectionMatrix = RenderSystem.getProjectionMatrix();
    }

}