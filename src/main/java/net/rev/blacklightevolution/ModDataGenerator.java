package net.rev.blacklightevolution;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.rev.blacklightevolution.datagen.ModEnUsLangProvider;
import net.rev.blacklightevolution.datagen.ModItemModelsProvider;
import net.rev.blacklightevolution.datagen.ModRecipesProvider;
import net.rev.blacklightevolution.datagen.ModZhCnLangProvider;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = BlacklightEvolution.MOD_ID)
public class ModDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new ModRecipesProvider(packOutput, lookupProvider));

        generator.addProvider(event.includeClient(), new ModItemModelsProvider(packOutput, BlacklightEvolution.MOD_ID, existingFileHelper));
        generator.addProvider(event.includeClient(), new ModEnUsLangProvider(packOutput, BlacklightEvolution.MOD_ID, "en_us"));
        generator.addProvider(event.includeClient(), new ModZhCnLangProvider(packOutput, BlacklightEvolution.MOD_ID, "zh_cn"));

    }
}
