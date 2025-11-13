package net.rev.blacklightevolution.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.rev.blacklightevolution.BlacklightEvolution;

public record S2CPlaySoundPacket(SoundEvent sound, float volume, float pitch) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<S2CPlaySoundPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(BlacklightEvolution.MOD_ID, "play_sound"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CPlaySoundPacket> STREAM_CODEC = StreamCodec.composite(
            SoundEvent.DIRECT_STREAM_CODEC,
            S2CPlaySoundPacket::sound,
            ByteBufCodecs.FLOAT,
            S2CPlaySoundPacket::volume,
            ByteBufCodecs.FLOAT,
            S2CPlaySoundPacket::pitch,
            S2CPlaySoundPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
