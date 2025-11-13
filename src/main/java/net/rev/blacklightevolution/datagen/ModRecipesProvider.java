package net.rev.blacklightevolution.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.rev.blacklightevolution.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModRecipesProvider extends RecipeProvider {
    public ModRecipesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLACKLIGHT_VIRUS_SAMPLE)
                .pattern("@#@")
                .pattern("@ @")
                .pattern(" @ ")
                .define('#', ItemTags.PLANKS)
                .define('@', Items.IRON_INGOT)
                .unlockedBy(getHasName(ModItems.BLACKLIGHT_VIRUS_SAMPLE), has(ModItems.BLACKLIGHT_VIRUS_SAMPLE))
                .save(recipeOutput);
    }
}
