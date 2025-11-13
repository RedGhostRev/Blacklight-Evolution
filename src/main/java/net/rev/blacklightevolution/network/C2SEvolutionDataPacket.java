package net.rev.blacklightevolution.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rev.blacklightevolution.BlacklightEvolution;

public record C2SEvolutionDataPacket(boolean infected, int evolutionLevel) implements CustomPacketPayload {

    public static final Type<C2SEvolutionDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BlacklightEvolution.MOD_ID, "c2s_evolution_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SEvolutionDataPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            C2SEvolutionDataPacket::infected,
            ByteBufCodecs.INT,
            C2SEvolutionDataPacket::evolutionLevel,
            C2SEvolutionDataPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
