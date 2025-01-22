package com.supremacy.module.type.combat;

import com.google.inject.Inject;
import com.supremacy.bind.BindService;
import com.supremacy.event.core.EventBus;
import com.supremacy.event.events.type.game.HandleInputEvent;
import com.supremacy.event.events.type.game.PacketEvent;
import com.supremacy.event.events.type.game.PlayerPositionUpdateEvent;
import com.supremacy.mixin.ClientPlayerInteractionManagerAccessor;
import com.supremacy.module.CategoryEnum;
import com.supremacy.module.ModuleInfo;
import com.supremacy.module.SupremacyModule;
import com.supremacy.module.type.combat.killaura.ExampleModuleMode;
import com.supremacy.module.type.combat.killaura.IDontKnowMode;
import com.supremacy.processor.entity.EntityProcessor;
import com.supremacy.setting.linker.SettingLinkerFactory;
import com.supremacy.setting.registrar.SettingRegistrar;
import com.supremacy.setting.type.Setting;
import com.supremacy.util.RotationUtil;
import com.supremacy.util.Stopwatch;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

@ModuleInfo(
        name = "KillAura",
        description = "Automatically attacks entities around you",
        category = CategoryEnum.COMBAT
)
public final class KillAuraModule implements SupremacyModule {

    private final MinecraftClient minecraftClient;

    private final Setting<Number> range;

    private final Setting<Integer> minCps, maxCps;

    private final Setting<Boolean> test;
    private final Setting<Color> colorTest;

    private final Setting<ExampleModuleMode> exampleModuleModeSetting;

    private static final float RANGE = 3.0F;

    private static final int MIN_CPS = 8;
    private static final int MAX_CPS = 12;

    private Entity entity;

    private float[] rotations;

    private int ticksSinceUnblock;

    private boolean blocking = false;
    private boolean cancel = false;
    private boolean canAttack;

    @Inject
    public KillAuraModule(MinecraftClient minecraftClient, final SettingLinkerFactory settingLinkerFactory, final SettingRegistrar settingRegistrar, final BindService bindService, final EntityProcessor entityProcessor, final EventBus eventBus, final Stopwatch stopwatch) {
        this.minecraftClient = minecraftClient;
        bindService.bind(this, GLFW.GLFW_KEY_R);

        this.range = settingRegistrar.registerClampedSetting("Range", RANGE, 3, 6, 0.1F);

        this.minCps = settingRegistrar.registerClampedSetting("Min CPS", MIN_CPS, 4, 12, 1);
        this.maxCps = settingRegistrar.registerClampedSetting("Max CPS", MAX_CPS, 12, 20, 1);

        this.test = settingRegistrar.registerSetting("Test", false);

        this.colorTest = settingRegistrar.registerSetting("Color Test", Color.WHITE);

        final Collection<ExampleModuleMode> exampleModuleModes = new HashSet<>();

        exampleModuleModes.add(new IDontKnowMode());

        this.exampleModuleModeSetting = settingRegistrar.registerCollectableSetting("example", exampleModuleModes);

        settingLinkerFactory.createSettingLinker().link(this.minCps, this.maxCps);

        eventBus.constructLinker(this).link(event -> {
            final PlayerPositionUpdateEvent playerPositionUpdateEvent = (PlayerPositionUpdateEvent) event;

            if (playerPositionUpdateEvent.isPost() || this.minecraftClient.world == null || this.minecraftClient.player == null) {
                return;
            }

            entity = entityProcessor.process(this.minecraftClient.world.getEntities(), this.range.getValue().floatValue());

            if (entity == null || entity == this.minecraftClient.player) {
                return;
            }

            float[] rotations = RotationUtil.getRotations(entity);

            float[] prevRotations = {playerPositionUpdateEvent.getPrevYaw(), playerPositionUpdateEvent.getPrevPitch()};

            rotations = RotationUtil.applyGCD(rotations, prevRotations);

            rotations = RotationUtil.applySanity(rotations);

            minecraftClient.player.setYaw(rotations[0]);
            minecraftClient.player.setPitch(rotations[1]);

            this.rotations = rotations;
            this.canAttack = this.minecraftClient.player.distanceTo(entity) <= this.range.getValue().floatValue();
        }, PlayerPositionUpdateEvent.ID);

        eventBus.constructLinker(this).link(event -> {
            final HandleInputEvent handleInputEvent = (HandleInputEvent) event;

            if (handleInputEvent.isPre()) {
                ticksSinceUnblock++;
            }

            if (this.minecraftClient.world == null || this.minecraftClient.player == null) {
                return;
            }

            if (entity == null || entity == this.minecraftClient.player) {
                this.blocking = false;
                this.unblock(minecraftClient);
                return;
            }

            if (!(this.minecraftClient.player.distanceTo(entity) <= this.range.getValue().floatValue())) {
                this.unblock(minecraftClient);
                return;
            }

            if (handleInputEvent.isPre()) {
                this.unblock(minecraftClient);
            } else {
                final double cps = Math.random() * (this.maxCps.getValue() - this.minCps.getValue()) + this.minCps.getValue();
                if (this.canAttack) {
                    if (stopwatch.elapsed((long) (1000f / cps), true)) {
                        this.minecraftClient.interactionManager.attackEntity(this.minecraftClient.player, entity);
                        this.minecraftClient.player.swingHand(Hand.MAIN_HAND);
                    }
                }

                this.block(minecraftClient, rotations);
            }
        }, HandleInputEvent.ID);

        eventBus.constructLinker(this).link(event -> {
            final PacketEvent packetEvent = (PacketEvent) event;

            if (packetEvent.getPacketType() != PacketEvent.PacketType.OUTGOING) {
                return;
            }

            if (packetEvent.getPacket() instanceof PlayerActionC2SPacket action && this.ticksSinceUnblock < 3) {
                if (action.getAction().equals(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM)) {
                    packetEvent.setCancelled(true);
                }
            }
        }, PacketEvent.ID);

        suffixProvider.provideSuffix(this, "Single");
    }

    @Override
    public void onDisable() {
        SupremacyModule.super.onDisable();

        this.unblock(minecraftClient);
    }

    private void block(MinecraftClient minecraftClient, float[] rotation) {
        if (this.blocking) {
            return;
        }

        if (!(minecraftClient.player.getInventory().getMainHandStack().getItem() instanceof SwordItem)) {
            return;
        }

        ((ClientPlayerInteractionManagerAccessor) this.minecraftClient.interactionManager).callSendSequencedPacket(this.minecraftClient.world, sequence -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, sequence, rotation[0], rotation[1]));
        blocking = true;
    }

    private void unblock(MinecraftClient minecraftClient) {
        if (!this.blocking) {
            return;
        }

        if (!(minecraftClient.player.getInventory().getMainHandStack().getItem() instanceof SwordItem)) {
            return;
        }

        int selectedSlot = minecraftClient.player.getInventory().selectedSlot;
        minecraftClient.world.sendPacket(new UpdateSelectedSlotC2SPacket((selectedSlot + 1) % 9));
        minecraftClient.world.sendPacket(new UpdateSelectedSlotC2SPacket(selectedSlot));
        blocking = false;
        ticksSinceUnblock = 0;
    }
}