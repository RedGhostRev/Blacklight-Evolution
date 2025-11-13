package net.rev.blacklightevolution.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.rev.blacklightevolution.BlacklightEvolution;
import net.rev.blacklightevolution.data.GrabData;
import net.rev.blacklightevolution.handler.ConsumeHandler;
import net.rev.blacklightevolution.handler.GrabHandler;
import net.rev.blacklightevolution.manager.EvolutionManager;

import java.util.Optional;
import java.util.UUID;

@EventBusSubscriber
public class ModPackets {
    @SubscribeEvent
    public static void registerPackets(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(BlacklightEvolution.MOD_ID);

        // 播放声音
        registrar.playToClient(
                S2CPlaySoundPacket.TYPE,
                S2CPlaySoundPacket.STREAM_CODEC,
                (packet, context) -> {
                    context.enqueueWork(() -> {
                        var player = context.player();
                        if (player != null) {
                            player.playSound(packet.sound(), packet.volume(), packet.pitch());
                        }
                    });
                }
        );

        // 同步所有进化属性
        registrar.playToClient(
                S2CEvolutionDataPacket.TYPE,
                S2CEvolutionDataPacket.STREAM_CODEC,
                (packet, context) -> {
                    context.enqueueWork(() -> {
                        var player = context.player();
                        if (player != null) {
                            EvolutionManager evolutionManager = EvolutionManager.getInstance();
                            evolutionManager.setAll(player, packet.infected(), packet.evolutionLevel());
                        }
                    });
                }
        );

        // 设置所有进化属性
        registrar.playToServer(
                C2SEvolutionDataPacket.TYPE,
                C2SEvolutionDataPacket.STREAM_CODEC,
                (packet, context) -> {
                    context.enqueueWork(() -> {
                        var player = context.player();
                        if (player != null) {
                            EvolutionManager evolutionManager = EvolutionManager.getInstance();
                            evolutionManager.setAll(player, packet.infected(), packet.evolutionLevel());
                        }
                    });
                }
        );

        // 设置单个进化属性
        registrar.playToServer(
                C2SEvolutionSingleDataPacket.TYPE,
                C2SEvolutionSingleDataPacket.STREAM_CODEC,
                (packet, context) -> {
                    context.enqueueWork(() -> {
                        var player = context.player();
                        if (player != null) {
                            EvolutionManager evolutionManager = EvolutionManager.getInstance();
                            switch (packet.operation()) {
                                case SET_INFECTED -> {
                                    boolean infected = packet.getAsBoolean();
                                    evolutionManager.setInfected(player, infected);
                                }
                                case SET_EVOLUTION_LEVEL -> {
                                    int evolutionLevel = packet.getAsInt();
                                    evolutionManager.setEvolutionLevel(player, evolutionLevel);
                                }
                            }
                        }
                    });
                }
        );

        // 抓取
        registrar.playToServer(
                C2SGrabEntityPacket.TYPE,
                C2SGrabEntityPacket.STREAM_CODEC,
                (packet, context) -> {
                    context.enqueueWork(() -> {
                        ServerPlayer player = (ServerPlayer) context.player();
                        if (player != null) {
                            GrabData grabData = EvolutionManager.getInstance().getGrabData(player);
                            Optional<UUID> optionalUUID = packet.targetEntityUUID();
                            optionalUUID.ifPresentOrElse(
                                    uuid -> GrabHandler.handleGrab(player, player.serverLevel().getEntity(uuid)),
                                    () -> GrabHandler.release(player, grabData)
                            );
                        }
                    });
                }
        );

        // 同步抓取数据
        registrar.playToClient(
                S2CGrabEntityPacket.TYPE,
                S2CGrabEntityPacket.STREAM_CODEC,
                (packet, context) -> {
                    context.enqueueWork(() ->
                            ClientGrabPending.applyOrQueue(
                                    packet.targetEntityId(),
                                    packet.playerUUID(),
                                    packet.targetEntityUUID()
                            )
                    );
                }
        );

        // 吞噬
        registrar.playToServer(
                C2SConsumeEntityPacket.TYPE,
                C2SConsumeEntityPacket.STREAM_CODEC,
                (packet, context) -> {
                    context.enqueueWork(() -> {
                        ServerPlayer player = (ServerPlayer) context.player();
                        if (player != null) {
                            ConsumeHandler.handleConsume(player, packet.targetEntityUUID());
                        }
                    });
                }
        );
    }
}
