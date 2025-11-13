package net.rev.blacklightevolution.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rev.blacklightevolution.BlacklightEvolution;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(BlacklightEvolution.MOD_ID);

    // 病毒样本
    public static final DeferredItem<Item> BLACKLIGHT_VIRUS_SAMPLE =
            ITEMS.register("blacklight_virus_sample", BlacklightVirusSampleItem::new);

    public static final DeferredItem<Item> BLACKLIGHT_VIRUS_SERUM =
            ITEMS.register("blacklight_virus_serum", BlacklightVirusSerumItem::new);

    public static final DeferredItem<Item> GUI_TEST =
            ITEMS.register("gui_test", ModGuiTestItem::new);

    public static final DeferredItem<Item> DEBUG_TEST =
            ITEMS.register("debug_test", DebugItem::new);

    public static final DeferredItem<Item> EVOLUTION_LEVEL_SETTER =
            ITEMS.register("evolution_level_setter", EvolutionLevelSetter::new);

    public static void register(IEventBus eventBus) {
            ITEMS.register(eventBus);
    }
}
