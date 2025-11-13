package net.rev.blacklightevolution.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rev.blacklightevolution.BlacklightEvolution;

import java.util.UUID;

public record C2SConsumeEntityPacket(UUID targetEntityUUID) implements CustomPacketPayload {
    public static final Type<C2SConsumeEntityPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BlacklightEvolution.MOD_ID, "c2s_consume_entity_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SConsumeEntityPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeUUID(packet.targetEntityUUID),
                    buf -> {
                        UUID uuid = buf.readUUID();
                        return new C2SConsumeEntityPacket(uuid);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
