package com.supremacy.mixin;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.supremacy.event.core.EventBus;
import com.supremacy.event.events.type.game.PacketEvent;
import com.supremacy.provider.provider.InjectorProvider;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {

    @Unique
    private static final Provider<Injector> injectorProvider = InjectorProvider.getInstance();

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void onHandlePacket(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {

        final Injector injector = injectorProvider.get();

        final EventBus eventBus = injector.getInstance(EventBus.class);

        if (packet instanceof BundleS2CPacket packs) {
            for (Iterator<Packet<? super ClientPlayPacketListener>> it = packs.getPackets().iterator(); it.hasNext(); ) {
                final PacketEvent packetEvent = new PacketEvent(packet, PacketEvent.PacketType.INCOMING);
                eventBus.publish(packetEvent);
                if (packetEvent.isCancelled()) {
                    it.remove();
                }
            }
        } else {
            final PacketEvent packetEvent = new PacketEvent(packet, PacketEvent.PacketType.INCOMING);
            eventBus.publish(packetEvent);
            if (packetEvent.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void send(Packet<?> packet, CallbackInfo ci) {

        final Injector injector = injectorProvider.get();

        final EventBus eventBus = injector.getInstance(EventBus.class);

        final PacketEvent packetEvent = new PacketEvent(packet, PacketEvent.PacketType.OUTGOING);
        eventBus.publish(packetEvent);
        if (packetEvent.isCancelled()) {
            ci.cancel();
        }
    }
}
