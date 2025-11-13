package net.rev.blacklightevolution.handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.rev.blacklightevolution.data.EntityGrabData;
import net.rev.blacklightevolution.data.GrabData;
import net.rev.blacklightevolution.data.ModAttachmentTypes;
import net.rev.blacklightevolution.manager.EvolutionManager;
import net.rev.blacklightevolution.network.S2CGrabEntityPacket;
import net.rev.blacklightevolution.util.CollisionUtil;
import net.rev.blacklightevolution.util.GrabbedAiUtil;

import java.util.Optional;

public class GrabHandler {
    public static void handleGrab(ServerPlayer player, Entity target) {
        EvolutionManager evolutionManager = EvolutionManager.getInstance();
        if (canGrab(player, target)) {
            GrabData grabData = evolutionManager.getGrabData(player);
            if (grabData.isGrabbing()) {
                release(player, grabData);
            } else {
                grab(player, (Mob) target, grabData);
            }
        }
    }

    public static boolean canGrab(Player player, Entity target) {
        EvolutionManager evolutionManager = EvolutionManager.getInstance();
        if (!evolutionManager.isInfected(player)) {
            return false;
        }
        if (!(target instanceof Mob mob)) {
            return false;
        }
        if (mob.getData(ModAttachmentTypes.ENTITY_GRAB_DATA).isGrabbed()) {
            return false;
        }
        if (!mob.isAlive()) {
            return false;
        }
        if (mob.getHealth() > mob.getMaxHealth() * 0.3F) {
            return false;
        }
        return true;
    }

    public static void grab(ServerPlayer player, LivingEntity target, GrabData grabData) {
        EntityGrabData entityGrabData = target.getData(ModAttachmentTypes.ENTITY_GRAB_DATA);
        if (entityGrabData.isGrabbed()) {
            //player.sendSystemMessage(Component.literal("这个生物正被其他玩家抓取"));
            return;
        }
        grabData.setGrabbedEntityUUID(target.getUUID());
        entityGrabData.setGrabberPlayerUUID(player.getUUID());
        if (target instanceof Mob mob) {
            CollisionUtil.enableNoEntityCollision(mob);
            GrabbedAiUtil.installIfNeeded(mob);
        }
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, new S2CGrabEntityPacket(Optional.of(player.getUUID()), Optional.of(target.getUUID()), target.getId()));
        //player.sendSystemMessage(Component.literal("你抓起了" + target.getDisplayName().getString()));
    }

    public static void updateGrabbedEntityPosition(ServerPlayer player, Mob mob) {
        mob.setTarget(null);
        mob.getNavigation().stop();
        mob.setAggressive(false);
        if (mob instanceof Creeper creeper) {
            creeper.setSwellDir(-1);
        }
        double followDistance = 3.0;
        double followHeight = 1.2;
        float strength = 0.8F; // 牵引强度
        // 计算目标位置
        Vec3 lookVec = player.getLookAngle();
        Vec3 playerPos = player.position();
        Vec3 targetPos = playerPos.add(
                lookVec.x * followDistance,
                player.getEyeHeight() + lookVec.y * followDistance - followHeight,
                lookVec.z * followDistance
        );

        // 计算牵引力
        Vec3 currentPos = mob.position();

        Vec3 force = targetPos.subtract(currentPos).scale(strength);

        // 平滑速度过渡
        Vec3 currentMotion = mob.getDeltaMovement();
        Vec3 newMotion = force.subtract(currentMotion).scale(0.3).add(currentMotion);

        // 速度限制
        double maxSpeedChange = 0.5;
        Vec3 delta = newMotion.subtract(currentMotion);
        Vec3 clampedDelta = new Vec3(
                Mth.clamp(delta.x, -maxSpeedChange, maxSpeedChange),
                Mth.clamp(delta.y, -maxSpeedChange, maxSpeedChange),
                Mth.clamp(delta.z, -maxSpeedChange, maxSpeedChange)
        );

        // 应用最终速度
        mob.setDeltaMovement(currentMotion.add(clampedDelta));
        mob.hurtMarked = true; // 同步速度变化
        mob.resetFallDistance();

        // 看向玩家
        mob.lookAt(player, 30, 15);
    }

    public static void release(ServerPlayer player, GrabData grabData) {
        grabData.getGrabbedEntityUUID().ifPresent(entityUUID -> {
            Entity entity = player.serverLevel().getEntity(entityUUID);
            if (entity instanceof LivingEntity) {
                EntityGrabData entityGrabData = entity.getData(ModAttachmentTypes.ENTITY_GRAB_DATA);
                entityGrabData.clearGrabberPlayerUUID();
                grabData.clearGrabbedEntityUUID();
                if (entity instanceof Mob mob) {
                    CollisionUtil.disableNoEntityCollision(mob);
                    GrabbedAiUtil.uninstallIfNeeded(mob);
                }
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new S2CGrabEntityPacket(Optional.empty(), Optional.empty(), entity.getId()));
                //player.sendSystemMessage(Component.literal("你放下了" + entity.getDisplayName().getString()));
            } else {
                grabData.clearGrabbedEntityUUID();
                if (entity == null) {
                    CollisionUtil.disableNoEntityCollision(player.serverLevel().getScoreboard(), entityUUID);
                    player.connection.send(new S2CGrabEntityPacket(Optional.empty(), Optional.empty(), -1));
                } else {
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new S2CGrabEntityPacket(Optional.of(player.getUUID()), Optional.empty(), entity.getId()));
                }
                //player.sendSystemMessage(Component.literal("执行强制清除抓取状态"));
            }
        });
    }

    public static void releaseBy(LivingEntity entity, EntityGrabData entityGrabData) {
        entityGrabData.getGrabberPlayerUUID().ifPresent(playerUUID -> {
            Player player = entity.level().getPlayerByUUID(playerUUID);
            if (player instanceof ServerPlayer serverPlayer) {
                GrabData grabData = EvolutionManager.getInstance().getGrabData(serverPlayer);
                if (grabData.isGrabbing() &&
                        grabData.getGrabbedEntityUUID().
                                map(uuid -> uuid.equals(entity.getUUID())).orElse(false)) {
                    grabData.clearGrabbedEntityUUID();
                }
                entityGrabData.clearGrabberPlayerUUID();
                CollisionUtil.disableNoEntityCollision((Mob) entity);
                GrabbedAiUtil.uninstallIfNeeded((Mob) entity);
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new S2CGrabEntityPacket(Optional.empty(), Optional.empty(), entity.getId()));
                //player.sendSystemMessage(Component.literal("被" + player.getDisplayName().getString() + "抓取的" + entity.getDisplayName().getString() + "死了"));
            }
        });
    }
}
