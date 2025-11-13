package net.rev.blacklightevolution.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.rev.blacklightevolution.manager.EvolutionManager;

public class BlacklightVirusSerumItem extends Item {
    public BlacklightVirusSerumItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            EvolutionManager evolutionManager = EvolutionManager.getInstance();
            boolean result = evolutionManager.setInfected(player, false);
            if (result) {
                stack.shrink(1);
                return InteractionResultHolder.consume(stack);
            }
        } else {
            return InteractionResultHolder.fail(stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
