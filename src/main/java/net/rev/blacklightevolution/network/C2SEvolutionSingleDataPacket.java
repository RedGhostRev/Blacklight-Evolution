package net.rev.blacklightevolution.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rev.blacklightevolution.BlacklightEvolution;

public record C2SEvolutionSingleDataPacket(Operation operation, Object value) implements CustomPacketPayload {

    public enum Operation {
        SET_INFECTED,
        SET_EVOLUTION_LEVEL
    }

    public static final Type<C2SEvolutionSingleDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BlacklightEvolution.MOD_ID, "c2s_evolution_single_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SEvolutionSingleDataPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeEnum(packet.operation);
                        switch (packet.operation) {
                            case SET_INFECTED -> buf.writeBoolean((Boolean) packet.value);
                            case SET_EVOLUTION_LEVEL -> buf.writeInt((Integer) packet.value);
                        }
                    },
                    buf -> {
                        Operation operation = buf.readEnum(Operation.class);
                        Object value = switch (operation) {
                            case SET_INFECTED -> buf.readBoolean();
                            case SET_EVOLUTION_LEVEL -> buf.readInt();
                        };
                        return new C2SEvolutionSingleDataPacket(operation, value);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public boolean getAsBoolean() {
        return (Boolean) value;
    }

    public int getAsInt() {
        return (Integer) value;
    }

}
