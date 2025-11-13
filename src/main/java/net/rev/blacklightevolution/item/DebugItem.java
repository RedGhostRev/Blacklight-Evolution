package net.rev.blacklightevolution.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.rev.blacklightevolution.manager.EvolutionManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class DebugItem extends Item {

    public static ArrayList<Field> list = new ArrayList<>();

    public DebugItem() {
        super(new Item.Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide) {
            EvolutionManager evolutionManager = EvolutionManager.getInstance();
            HashMap<MutableComponent, MutableComponent> map = evolutionManager.getAll(player);
            map.forEach((key, value) -> player.sendSystemMessage(
                    Component
                            .empty()
                            .append(key.copy())
                            .append(": ")
                            .append(value.copy())));
        }
        return super.use(level, player, usedHand);
    }
}
