package com.supremacy.event.events.type.game;

import com.supremacy.event.events.AbstractCancellableEvent;
import net.minecraft.network.packet.Packet;

public class PacketEvent extends AbstractCancellableEvent {

    public static final int ID = 5;

    private final Packet<?> packet;

    private final PacketType packetType;

    public PacketEvent(final Packet<?> packet, final PacketType packetType) {
        this.packet = packet;
        this.packetType = packetType;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public enum PacketType {
        INCOMING,
        OUTGOING
    }

    @Override
    public int id() {
        return ID;
    }
}
