package net.rev.blacklightevolution.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rev.blacklightevolution.BlacklightEvolution;

import java.util.Optional;
import java.util.UUID;

public record C2SGrabEntityPacket(Optional<UUID> targetEntityUUID) implements CustomPacketPayload {
    public static final Type<C2SGrabEntityPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BlacklightEvolution.MOD_ID, "c2s_grab_entity_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SGrabEntityPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) ->
                            buf.writeOptional(
                                    packet.targetEntityUUID,
                                    (ubuf, uuid) -> ubuf.writeUUID(uuid)
                            )
                    ,
                    buf -> new C2SGrabEntityPacket(
                            buf.readOptional(ubuf -> ubuf.readUUID())
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
