package net.rev.blacklightevolution.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.rev.blacklightevolution.gui.GuiOpenWrapper;

public class EvolutionLevelSetter extends Item {
    public EvolutionLevelSetter() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide) {
            GuiOpenWrapper.openGui();
        }
        return InteractionResultHolder.pass(stack);
    }
}
