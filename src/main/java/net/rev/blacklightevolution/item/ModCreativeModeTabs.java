package net.rev.blacklightevolution.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rev.blacklightevolution.BlacklightEvolution;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BlacklightEvolution.MOD_ID);

    public static final Supplier<CreativeModeTab> BLACKLIGHT_EVOLUTION_TAB =
            CREATIVE_MODE_TABS.register("blacklight_evolution_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.BLACKLIGHT_VIRUS_SAMPLE.get()))
                    .title(Component.translatable("itemGroup.blacklight_evolution_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.BLACKLIGHT_VIRUS_SAMPLE);
                        output.accept(ModItems.BLACKLIGHT_VIRUS_SERUM);
                        output.accept(ModItems.GUI_TEST);
                        output.accept(ModItems.DEBUG_TEST);
                        output.accept(ModItems.EVOLUTION_LEVEL_SETTER);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
