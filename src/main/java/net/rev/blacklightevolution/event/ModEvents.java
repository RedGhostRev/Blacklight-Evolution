package net.rev.blacklightevolution.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.rev.blacklightevolution.data.EntityGrabData;
import net.rev.blacklightevolution.data.EvolutionData;
import net.rev.blacklightevolution.data.GrabData;
import net.rev.blacklightevolution.data.ModAttachmentTypes;
import net.rev.blacklightevolution.handler.GrabHandler;
import net.rev.blacklightevolution.manager.EvolutionManager;
import net.rev.blacklightevolution.network.S2CEvolutionDataPacket;
import net.rev.blacklightevolution.network.S2CGrabEntityPacket;

import java.util.Optional;

@EventBusSubscriber
public class ModEvents {
    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }
        Entity target = event.getTarget();
        EntityGrabData entityGrabData = target.getData(ModAttachmentTypes.ENTITY_GRAB_DATA);
        serverPlayer.connection.send(new S2CGrabEntityPacket(
                entityGrabData.getGrabberPlayerUUID(),
                Optional.of(target.getUUID()),
                target.getId())
        );
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        event.getServer().getPlayerList().getPlayers().forEach(player -> {
            if (player instanceof ServerPlayer serverPlayer) {
                GrabData grabData = EvolutionManager.getInstance().getGrabData(serverPlayer);
                if (grabData.isGrabbing()) {
                    grabData.getGrabbedEntityUUID().ifPresent(grabbedEntityUUID -> {
                        Entity entity = serverPlayer.serverLevel().getEntity(grabbedEntityUUID);
                        if (entity instanceof Mob mob && mob.isAlive()) {
                            GrabHandler.updateGrabbedEntityPosition(serverPlayer, mob);
                        } else {
                            GrabHandler.release(serverPlayer, grabData);
                        }
                    });
                }
            }
        });
    }

    @SubscribeEvent
    public static void onEntityTeleport(EntityTeleportEvent event) {
        EntityGrabData entityGrabData = event.getEntity().getData(ModAttachmentTypes.ENTITY_GRAB_DATA);
        if (entityGrabData.isGrabbed()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            EvolutionData infectionData = serverPlayer.getData(ModAttachmentTypes.EVOLUTION_DATA);
            serverPlayer.connection.send(new S2CEvolutionDataPacket(infectionData.isInfected(), infectionData.getEvolutionLevel()));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            Player originalPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();
            EvolutionManager evolutionManager = EvolutionManager.getInstance();
            evolutionManager.setAll(originalPlayer, newPlayer);

            if (newPlayer instanceof ServerPlayer serverPlayer) {
                serverPlayer.server.execute(() ->
                        serverPlayer.connection.send(new S2CEvolutionDataPacket(
                                evolutionManager.isInfected(serverPlayer),
                                evolutionManager.getEvolutionLevel(serverPlayer)
                        )));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            GrabData grabData = EvolutionManager.getInstance().getGrabData(serverPlayer);
            if (grabData.isGrabbing()) {
                GrabHandler.release(serverPlayer, grabData);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ServerPlayer serverPlayer) {
            GrabData grabData = EvolutionManager.getInstance().getGrabData(serverPlayer);
            if (grabData.isGrabbing()) {
                GrabHandler.release(serverPlayer, grabData);
            }
            GrabHandler.release(serverPlayer, EvolutionManager.getInstance().getGrabData(serverPlayer));
        } else {
            LivingEntity livingEntity = event.getEntity();
            EntityGrabData entityGrabData = livingEntity.getData(ModAttachmentTypes.ENTITY_GRAB_DATA);
            if (entityGrabData.isGrabbed()) {
                GrabHandler.releaseBy(livingEntity, entityGrabData);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity.getRemovalReason() == Entity.RemovalReason.KILLED) {
            return;
        }
        if (entity instanceof ServerPlayer serverPlayer) {
            GrabData grabData = EvolutionManager.getInstance().getGrabData(serverPlayer);
            if (grabData.isGrabbing()) {
                GrabHandler.release(serverPlayer, grabData);
            }
            GrabHandler.release(serverPlayer, EvolutionManager.getInstance().getGrabData(serverPlayer));
        } else if (entity instanceof LivingEntity livingEntity) {
            EntityGrabData entityGrabData = livingEntity.getData(ModAttachmentTypes.ENTITY_GRAB_DATA);
            if (entityGrabData.isGrabbed()) {
                GrabHandler.releaseBy(livingEntity, entityGrabData);
            }
        }
    }
}
