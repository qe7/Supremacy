package com.supremacy.module.type.combat;

import com.google.inject.Inject;
import com.supremacy.event.core.EventBus;
import com.supremacy.event.events.type.game.PacketEvent;
import com.supremacy.module.CategoryEnum;
import com.supremacy.module.ModuleInfo;
import com.supremacy.module.SupremacyModule;
import com.supremacy.setting.registrar.SettingRegistrar;
import com.supremacy.setting.type.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.text.Text;

@ModuleInfo(
    name = "Velocity",
    description = "Velocity",
    category = CategoryEnum.COMBAT
)
public final class VelocityModule implements SupremacyModule {

    private final Setting<Integer> horizontalVelocity, verticalVelocity;

    @Inject
    public VelocityModule(MinecraftClient minecraftClient, final SettingRegistrar settingRegistrar, final EventBus eventBus) {
        this.horizontalVelocity = settingRegistrar.registerClampedSetting("Horizontal Velocity", 0, 0, 100, 1);
        this.verticalVelocity = settingRegistrar.registerClampedSetting("Vertical Velocity", 0, 0, 100, 1);

        eventBus.constructLinker(this).link(event -> {
            final PacketEvent packetEvent = (PacketEvent) event;

            if (minecraftClient.player == null) return;

            if (packetEvent.getPacketType() != PacketEvent.PacketType.INCOMING) return;

            if (packetEvent.getPacket() instanceof EntityVelocityUpdateS2CPacket velocityPacket) {
                if (velocityPacket.getEntityId() != minecraftClient.player.getId()) return;


                double velocityX = minecraftClient.player.getVelocity().x, velocityY = minecraftClient.player.getVelocity().y, velocityZ = minecraftClient.player.getVelocity().z;

                if (this.verticalVelocity.getValue() != 0) {
                    double vertical = this.verticalVelocity.getValue() / 100.0D;
                    velocityY = velocityPacket.getVelocityY() * vertical;
                }

                if (this.horizontalVelocity.getValue() != 0) {
                    double horizontal = this.horizontalVelocity.getValue() / 100.0D;
                    velocityX = velocityPacket.getVelocityX() * horizontal;
                    velocityZ = velocityPacket.getVelocityZ() * horizontal;
                }

                minecraftClient.player.setVelocity(velocityX, velocityY, velocityZ);
                packetEvent.cancel();
            }
        }, PacketEvent.ID);
    }
}
