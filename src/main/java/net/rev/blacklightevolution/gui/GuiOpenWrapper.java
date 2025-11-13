package net.rev.blacklightevolution.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.rev.blacklightevolution.languagekey.ModGuiKeys;

@OnlyIn(Dist.CLIENT)
public class GuiOpenWrapper {
    public static void openGui() {
        Minecraft.getInstance().setScreen(new EvolutionSetterGui(Component.translatable(ModGuiKeys.EVOLUTION_LEVEL_SETTER_TITLE)));
    }
}
