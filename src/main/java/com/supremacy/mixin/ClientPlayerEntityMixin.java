package com.supremacy.mixin;

import com.google.inject.Injector;
import com.mojang.authlib.GameProfile;
import com.supremacy.event.core.EventBus;
import com.supremacy.event.events.type.game.PlayerPositionUpdateEvent;
import com.supremacy.provider.provider.InjectorProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {


    @Shadow protected abstract boolean isCamera();

    @Shadow protected abstract void sendSprintingPacket();

    @Shadow private boolean lastSneaking;

    @Shadow private double lastX;
    @Shadow private double lastBaseY;
    @Shadow private double lastZ;
    @Shadow private float lastYaw;
    @Shadow private float lastPitch;
    @Shadow private int ticksSinceLastPositionPacketSent;
    @Shadow private boolean lastOnGround;
    @Shadow private boolean autoJumpEnabled;

    @Unique
    private PlayerPositionUpdateEvent playerMoveEvent;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    private void onMotion(CallbackInfo ci) {
        ClientPlayerEntityMixin localPlayer = this;

        final Injector injector = InjectorProvider.getInstance().get();

        final EventBus eventBus = injector.getInstance(EventBus.class);

        if (localPlayer.isCamera()) {
            ci.cancel(); // Always cancel so we can run our own events

            final ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler == null) return;

            localPlayer.sendSprintingPacket();
            boolean bl = this.isSneaking();
            if (bl != localPlayer.lastSneaking) {
                ClientCommandC2SPacket.Mode mode = bl ? ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY : ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY;
                networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode));
                this.lastSneaking = bl;
            }

            playerMoveEvent = new PlayerPositionUpdateEvent(this.getYaw(), this.getPitch(), this.prevYaw, this.prevPitch, this.getX(), this.getY(), this.getZ(), this.isOnGround(), this.horizontalCollision);
            playerMoveEvent.setState(false);
            eventBus.publish(playerMoveEvent);

            boolean rotated;
            double d = playerMoveEvent.getPosX() - this.lastX;
            double e = playerMoveEvent.getPosY() - this.lastBaseY;
            double f = playerMoveEvent.getPosZ() - this.lastZ;
            double g = playerMoveEvent.getYaw() - this.lastYaw;
            double h = playerMoveEvent.getPitch() - this.lastPitch;
            ++this.ticksSinceLastPositionPacketSent;
            boolean moved = MathHelper.squaredMagnitude(d, e, f) > MathHelper.square(2.0E-4) || this.ticksSinceLastPositionPacketSent >= 20;
            boolean bl4 = rotated = g != 0.0 || h != 0.0;
            if (this.hasVehicle()) {
                Vec3d vec3d = this.getVelocity();
                networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(vec3d.x, -999.0, vec3d.z, playerMoveEvent.getYaw(), playerMoveEvent.getPitch(), playerMoveEvent.isOnGround(), playerMoveEvent.isHorizontal()));
                moved = false;
            } else if (moved && rotated) {
                networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(playerMoveEvent.getPosX(), playerMoveEvent.getPosY(), playerMoveEvent.getPosZ(), playerMoveEvent.getYaw(), playerMoveEvent.getPitch(), playerMoveEvent.isOnGround(), playerMoveEvent.isHorizontal()));
            } else if (moved) {
                networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(playerMoveEvent.getPosX(), playerMoveEvent.getPosY(), playerMoveEvent.getPosZ(), playerMoveEvent.isOnGround(), playerMoveEvent.isHorizontal()));
            } else if (rotated) {
                networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(playerMoveEvent.getYaw(), playerMoveEvent.getPitch(), playerMoveEvent.isOnGround(), playerMoveEvent.isHorizontal()));
            } else if (this.lastOnGround != playerMoveEvent.isOnGround()) {
                networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(playerMoveEvent.isOnGround(), playerMoveEvent.isHorizontal()));
            }
            if (moved) {
                this.lastX = playerMoveEvent.getPosX();
                this.lastBaseY = playerMoveEvent.getPosY();
                this.lastZ = playerMoveEvent.getPosZ();
                this.ticksSinceLastPositionPacketSent = 0;
            }
            if (rotated) {
                this.lastYaw = playerMoveEvent.getYaw();
                this.lastPitch = playerMoveEvent.getPitch();
            }
            this.lastOnGround = playerMoveEvent.isOnGround();
            this.autoJumpEnabled = MinecraftClient.getInstance().options.getAutoJump().getValue();


            playerMoveEvent = new PlayerPositionUpdateEvent(this.getYaw(), this.getPitch(), this.prevYaw, this.prevPitch, this.getX(), this.getY(), this.getZ(), this.isOnGround(), this.horizontalCollision);
            playerMoveEvent.setState(true);
            eventBus.publish(playerMoveEvent);
        }
    }


}