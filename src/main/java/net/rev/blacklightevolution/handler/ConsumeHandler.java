package net.rev.blacklightevolution.handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodData;
import net.neoforged.neoforge.network.PacketDistributor;
import net.rev.blacklightevolution.data.EntityGrabData;
import net.rev.blacklightevolution.data.GrabData;
import net.rev.blacklightevolution.data.ModAttachmentTypes;
import net.rev.blacklightevolution.manager.EvolutionManager;
import net.rev.blacklightevolution.network.S2CGrabEntityPacket;
import net.rev.blacklightevolution.util.CollisionUtil;

import java.util.Optional;
import java.util.UUID;

public class ConsumeHandler {
    public static void handleConsume(ServerPlayer player, UUID targetEntityUUID) {
        Entity entity = player.serverLevel().getEntity(targetEntityUUID);
        if (!(entity instanceof Mob mob) || !mob.isAlive()) {
            return;
        }
        EntityGrabData entityGrabData = mob.getData(ModAttachmentTypes.ENTITY_GRAB_DATA);
        if (!entityGrabData.getGrabberPlayerUUID().map(player.getUUID()::equals).orElse(false)) {
            return;
        }
        consume(player, mob);
    }

//    public static boolean canConsume(ServerPlayer player, LivingEntity target) {
//        EvolutionManager evolutionManager = EvolutionManager.getInstance();
//        if (!evolutionManager.isInfected(player)) {
//            return false;
//        }
//        if (!target.isAlive()) {
//            return false;
//        }
//        if (target.getHealth() > target.getMaxHealth() * 0.2F) {
//            return false;
//        }
//        if (player.distanceTo(target) > 3.0F) {
//            return false;
//        }
//        return true;
//    }

    public static void consume(ServerPlayer player, Mob target) {
        EntityGrabData entityGrabData = target.getData(ModAttachmentTypes.ENTITY_GRAB_DATA);
        GrabData grabData = EvolutionManager.getInstance().getGrabData(player);
        grabData.clearGrabbedEntityUUID();
        entityGrabData.clearGrabberPlayerUUID();
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                target,
                new S2CGrabEntityPacket(
                        Optional.empty(),
                        Optional.empty(),
                        target.getId()
                )
        );
        CollisionUtil.disableNoEntityCollision(player.serverLevel().getScoreboard(), target.getUUID());
        target.discard();
        player.killedEntity(player.serverLevel(), target);
        float healAmount = target.getMaxHealth() * 0.2F;
        int foodAmount = (int) (healAmount * 1.25F);
        float saturationAmount = healAmount * 0.85F;
        player.heal(healAmount);
        FoodData foodData = player.getFoodData();
        int newFoodLevel = Math.min(foodAmount + foodData.getFoodLevel(), 20);
        float newSaturation = Math.min(saturationAmount + foodData.getSaturationLevel(), 20F);
        foodData.setFoodLevel(newFoodLevel);
        foodData.setSaturation(newSaturation);
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0, false, false, true));
    }
}
