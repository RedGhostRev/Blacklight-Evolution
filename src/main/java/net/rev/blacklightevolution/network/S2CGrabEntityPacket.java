package net.rev.blacklightevolution.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rev.blacklightevolution.BlacklightEvolution;

import java.util.Optional;
import java.util.UUID;

public record S2CGrabEntityPacket(Optional<UUID> playerUUID, Optional<UUID> targetEntityUUID,
                                  int targetEntityId) implements CustomPacketPayload {
    public static final Type<S2CGrabEntityPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BlacklightEvolution.MOD_ID, "s2c_grab_entity_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CGrabEntityPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeOptional(
                                packet.playerUUID,
                                (ubuf, uuid) -> ubuf.writeUUID(uuid)
                        );
                        buf.writeOptional(
                                packet.targetEntityUUID,
                                (ubuf, uuid) -> ubuf.writeUUID(uuid)
                        );
                        buf.writeInt(packet.targetEntityId);
                    },
                    buf -> new S2CGrabEntityPacket(
                            buf.readOptional(ubuf -> ubuf.readUUID()),
                            buf.readOptional(ubuf -> ubuf.readUUID()),
                            buf.readInt()
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
