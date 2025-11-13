package net.rev.blacklightevolution.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rev.blacklightevolution.BlacklightEvolution;

public record S2CEvolutionDataPacket(boolean infected, int evolutionLevel) implements CustomPacketPayload {

    public static final Type<S2CEvolutionDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BlacklightEvolution.MOD_ID, "s2c_evolution_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CEvolutionDataPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            S2CEvolutionDataPacket::infected,
            ByteBufCodecs.INT,
            S2CEvolutionDataPacket::evolutionLevel,
            S2CEvolutionDataPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
