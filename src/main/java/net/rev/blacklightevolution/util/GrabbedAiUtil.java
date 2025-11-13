package net.rev.blacklightevolution.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.rev.blacklightevolution.data.EntityGrabData;
import net.rev.blacklightevolution.data.ModAttachmentTypes;

import java.util.*;

public class GrabbedAiUtil {
    private GrabbedAiUtil() {
    }

    private static final Map<Mob, Goal> INSTALLED = new WeakHashMap<>();

    public static void installIfNeeded(Mob mob) {
        if (!(mob.level() instanceof ServerLevel)) {
            return;
        }
        if (INSTALLED.containsKey(mob)) {
            return;
        }
        Goal goal = new SuppressGoal(mob);
        mob.goalSelector.addGoal(0, goal);
        INSTALLED.put(mob, goal);
    }

    public static void uninstallIfNeeded(Mob mob) {
        Goal goal = INSTALLED.remove(mob);
        if (goal != null) {
            mob.goalSelector.removeGoal(goal);
        }
    }

    private static final class SuppressGoal extends Goal {
        private final Mob mob;

        SuppressGoal(Mob mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return isGrabbed();
        }

        @Override
        public boolean canContinueToUse() {
            return isGrabbed();
        }

        @Override
        public void start() {
            mob.getNavigation().stop();
            mob.setTarget(null);
            mob.setAggressive(false);
        }

        @Override
        public void tick() {
            mob.getNavigation().stop();
            mob.setTarget(null);
            mob.setAggressive(false);

            if (mob instanceof Creeper creeper) {
                creeper.setSwellDir(-1);
            }
            mob.resetFallDistance();
            getGrabber().ifPresent(grabber -> mob.lookAt(grabber, 30, 15));
        }

        private boolean isGrabbed() {
            return mob.getData(ModAttachmentTypes.ENTITY_GRAB_DATA).isGrabbed();
        }

        private Optional<LivingEntity> getGrabber() {
            EntityGrabData entityGrabData = mob.getData(ModAttachmentTypes.ENTITY_GRAB_DATA);
            Optional<UUID> grabberPlayerUUID = entityGrabData.getGrabberPlayerUUID();
            if (grabberPlayerUUID.isEmpty()) {
                return Optional.empty();
            }
            Player player = mob.level().getPlayerByUUID(grabberPlayerUUID.get());
            return player == null ? Optional.empty() : Optional.of(player);
        }
    }
}
