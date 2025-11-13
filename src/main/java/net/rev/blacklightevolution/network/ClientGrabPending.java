package net.rev.blacklightevolution.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.rev.blacklightevolution.BlacklightEvolution;
import net.rev.blacklightevolution.data.EntityGrabData;
import net.rev.blacklightevolution.data.GrabData;
import net.rev.blacklightevolution.data.ModAttachmentTypes;
import net.rev.blacklightevolution.manager.EvolutionManager;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = BlacklightEvolution.MOD_ID, value = Dist.CLIENT)
public class ClientGrabPending {

    private record Pending(Optional<UUID> grabberUUID, Optional<UUID> targetEntityUUID) {}

    private static final Map<Integer, Pending> PENDING = new ConcurrentHashMap<>();

    public static void applyOrQueue(int entityId, Optional<UUID> grabberUUID, Optional<UUID> targetEntityUUID) {
        if (entityId == -1) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        Entity entity = minecraft.level.getEntity(entityId);
        if (entity == null) {
            PENDING.put(entityId, new Pending(grabberUUID, targetEntityUUID));
            return;
        }

        applyNow(entity, grabberUUID, targetEntityUUID);
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            return;
        }
        int id = event.getEntity().getId();
        Pending pending = PENDING.remove(id);
        if (pending != null) {
            applyNow(event.getEntity(), pending.grabberUUID, pending.targetEntityUUID);
        }
    }

    private static void applyNow(Entity entity, Optional<UUID> grabberUUID, Optional<UUID> targetEntityUUID) {
        // 实体被谁抓取
        EntityGrabData entityGrabData = entity.getData(ModAttachmentTypes.ENTITY_GRAB_DATA);
        grabberUUID.ifPresentOrElse(entityGrabData::setGrabberPlayerUUID, entityGrabData::clearGrabberPlayerUUID);

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        GrabData grabData = EvolutionManager.getInstance().getGrabData(player);
        boolean isSelf = grabberUUID.map(player.getUUID()::equals).orElse(false);

        // 判断抓取者是不是自己
        if (isSelf) {
            grabData.setGrabbedEntityUUID(targetEntityUUID.orElse(entity.getUUID()));
        } else {
            // 如果我本地记录的目标正好是这只实体，而现在被释放或被别人接手，则清理本地状态
            boolean wasThis = grabData.getGrabbedEntityUUID().map(entity.getUUID()::equals).orElse(false);
            if (wasThis) {
                grabData.clearGrabbedEntityUUID();
            }
        }
    }
}
