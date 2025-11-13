package net.rev.blacklightevolution.manager;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.rev.blacklightevolution.data.EvolutionData;
import net.rev.blacklightevolution.data.EvolutionStage;
import net.rev.blacklightevolution.data.GrabData;
import net.rev.blacklightevolution.data.ModAttachmentTypes;
import net.rev.blacklightevolution.languagekey.EvolutionKeys;
import net.rev.blacklightevolution.languagekey.ModMessageKeys;
import net.rev.blacklightevolution.network.S2CEvolutionDataPacket;
import net.rev.blacklightevolution.network.S2CPlaySoundPacket;

import java.util.LinkedHashMap;

public class EvolutionManager {
    private static final EvolutionManager INSTANCE = new EvolutionManager();

    private EvolutionManager() {
    }

    public static EvolutionManager getInstance() {
        return INSTANCE;
    }

    public GrabData getGrabData(Player player) {
        EvolutionData evolutionData = player.getData(ModAttachmentTypes.EVOLUTION_DATA);
        return evolutionData.getGrabData();
    }

    // 一次获取除抓取属性外所有属性
    public LinkedHashMap<MutableComponent, MutableComponent> getAll(Player player) {
        LinkedHashMap<MutableComponent, MutableComponent> map = new LinkedHashMap<>();
        EvolutionData evolutionData = getEvolutionData(player);
        map.put(Component.translatable(EvolutionKeys.INFECTION_STATUS),
                Component.translatable(evolutionData.isInfected() ?
                        EvolutionKeys.INFECTION_STATUS_TRUE : EvolutionKeys.INFECTION_STATUS_FALSE));
        map.put(Component.translatable(EvolutionKeys.EVOLUTION_LEVEL), Component.literal(evolutionData.getEvolutionLevel() + ""));
        map.put(Component.translatable(EvolutionKeys.EVOLUTION_STAGE),
                Component.translatable(EvolutionKeys.EVOLUTION_STAGE + "_" + getEvolutionStage(player).toString().toLowerCase()));
        return map;
    }

    // 一次设置除抓取属性外所有属性，忽略判断
    public void setAll(Player player, boolean infected, int evolutionLevel) {
        EvolutionData evolutionData = getEvolutionData(player);
        evolutionData.setInfected(infected);
        evolutionData.setEvolutionLevel(evolutionLevel);
    }

    // 一次设置除抓取属性外所有属性，两个玩家实体间的数据迁移，忽略判断
    public void setAll(Player originalPlayer, Player newPlayer) {
        if (originalPlayer instanceof ServerPlayer && newPlayer instanceof ServerPlayer) {

        }
        EvolutionData oldData = getEvolutionData(originalPlayer);
        EvolutionData newData = getEvolutionData(newPlayer);
        newData.setInfected(oldData.isInfected());
        newData.setEvolutionLevel(oldData.getEvolutionLevel());
    }

    public boolean isInfected(Player player) {
        return getEvolutionData(player).isInfected();
    }

    // 返回感染布尔值是否设置成功
    public boolean setInfected(Player player, boolean infected) {
        boolean oldInfected = getEvolutionData(player).isInfected();
        if (oldInfected == infected) {
            onInfectionSetFail(player, oldInfected);
            return false;
        }
        getEvolutionData(player).setInfected(infected);
        onInfectionChange(player, infected);
        return true;
    }

    public int getEvolutionLevel(Player player) {
        return getEvolutionData(player).getEvolutionLevel();
    }

    // 返回进化等级是否设置成功
    public boolean setEvolutionLevel(Player player, int evolutionLevel) {
        if (evolutionLevel < 0) {
            onEvolutionLevelFail(player, ModMessageKeys.EVOLUTION_LEVEL_SET_FAIL_LESS_THAN_ZERO);
            return false;
        } else if (evolutionLevel > 0 && !isInfected(player)) {
            onEvolutionLevelFail(player, ModMessageKeys.EVOLUTION_LEVEL_SET_FAIL_NOT_INFECTED);
            return false;
        }
        getEvolutionData(player).setEvolutionLevel(evolutionLevel);
        onEvolutionLevelChange(player, evolutionLevel);
        return true;
    }

    // 返回进化阶段
    public EvolutionStage getEvolutionStage(Player player) {
        boolean infected = isInfected(player);
        int evolutionLevel = getEvolutionLevel(player);
        return EvolutionStage.from(infected, evolutionLevel);
    }

    private void onEvolutionLevelChange(Player player, int evolutionLevel) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new S2CEvolutionDataPacket(isInfected(serverPlayer), evolutionLevel));
            serverPlayer.sendSystemMessage(Component.translatable(ModMessageKeys.EVOLUTION_LEVEL_SET, evolutionLevel));
        }
    }

    private void onEvolutionLevelFail(Player player, String reason) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.translatable(reason));
        }
    }


    private void onInfectionChange(Player player, boolean infected) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (infected) {
                serverPlayer.connection.send(new S2CPlaySoundPacket(SoundEvents.ZOMBIE_INFECT, 1.0F, 1.0F));
                serverPlayer.sendSystemMessage(Component.translatable(ModMessageKeys.INFECT));
            } else {
                if (getEvolutionLevel(serverPlayer) > 0) {
                    getEvolutionData(serverPlayer).setEvolutionLevel(0);
                }
                serverPlayer.connection.send(new S2CPlaySoundPacket(SoundEvents.ZOMBIE_VILLAGER_CURE, 1.0F, 1.0F));
                serverPlayer.sendSystemMessage(Component.translatable(ModMessageKeys.CURE));
            }
            serverPlayer.connection.send(new S2CEvolutionDataPacket(infected, getEvolutionLevel(serverPlayer)));
        }
    }

    private void onInfectionSetFail(Player player, boolean oldInfected) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (oldInfected) {
                serverPlayer.sendSystemMessage(Component.translatable(ModMessageKeys.INFECTED));
            } else {
                serverPlayer.sendSystemMessage(Component.translatable(ModMessageKeys.CURED));
            }
        }
    }

    private EvolutionData getEvolutionData(Player player) {
        return player.getData(ModAttachmentTypes.EVOLUTION_DATA);
    }
}
