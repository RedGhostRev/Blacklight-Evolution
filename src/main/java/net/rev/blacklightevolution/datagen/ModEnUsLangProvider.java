package net.rev.blacklightevolution.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.rev.blacklightevolution.item.ModItems;
import net.rev.blacklightevolution.languagekey.EvolutionKeys;
import net.rev.blacklightevolution.languagekey.ModGuiKeys;
import net.rev.blacklightevolution.languagekey.ModKeyBindingsKeys;
import net.rev.blacklightevolution.languagekey.ModMessageKeys;

public class ModEnUsLangProvider extends LanguageProvider {
    public ModEnUsLangProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        // Item
        add(ModItems.BLACKLIGHT_VIRUS_SAMPLE.get(), "Blacklight Virus Sample");
        add(ModItems.BLACKLIGHT_VIRUS_SERUM.get(), "Blacklight Virus Serum");
        add(ModItems.EVOLUTION_LEVEL_SETTER.get(), "Evolution Level Setter");

        // Message
        add(ModMessageKeys.INFECT, "§4You've been infected!");
        add(ModMessageKeys.INFECTED, "You'd been infected before.");
        add(ModMessageKeys.CURE, "§2You've been cured!！");
        add(ModMessageKeys.CURED, "You're still healthy.");
        add(ModMessageKeys.EVOLUTION_LEVEL_SET, "Your Evolution Level has been set to %1$d.");
        add(ModMessageKeys.EVOLUTION_LEVEL_SET_FAIL_NOT_INFECTED, "Set failed: You've not been infected.");
        add(ModMessageKeys.EVOLUTION_LEVEL_SET_FAIL_LESS_THAN_ZERO, "Set failed: Evolution Level cannot be less than 0.");

        // Evolution Data
        // Infection Status
        add(EvolutionKeys.INFECTION_STATUS, "Infection Status");
        add(EvolutionKeys.INFECTION_STATUS_TRUE, "Infected");
        add(EvolutionKeys.INFECTION_STATUS_FALSE, "Uninfected");
        // Evolution Level
        add(EvolutionKeys.EVOLUTION_LEVEL, "Evolution Level");
        // Evolution Stage
        add(EvolutionKeys.EVOLUTION_STAGE, "Evolution Stage");
        add(EvolutionKeys.EVOLUTION_STAGE_HUMAN, "Human");
        add(EvolutionKeys.EVOLUTION_STAGE_INFILTRATOR, "Infiltrator");
        add(EvolutionKeys.EVOLUTION_STAGE_INVADER, "Invader");
        add(EvolutionKeys.EVOLUTION_STAGE_SLAYER, "Slayer");
        add(EvolutionKeys.EVOLUTION_STAGE_REAPER, "Reaper");
        add(EvolutionKeys.EVOLUTION_STAGE_ANNIHILATOR, "Annihilator");
        add(EvolutionKeys.EVOLUTION_STAGE_ULTIMATE_PROTOTYPE, "Ultimate Prototype");

        // KeyBindings
        // Key Category
        add(ModKeyBindingsKeys.ABILITIES, "Blacklight: Evolution - Abilities");
        // Key
        add(ModKeyBindingsKeys.GRAB, "Grab");
        add(ModKeyBindingsKeys.CONSUME, "Consume");

        // GUI Text
        // Evolution Level Setter GUI
        add(ModGuiKeys.EVOLUTION_LEVEL_SETTER_TITLE, "Evolution Level Setting");
        add(ModGuiKeys.EVOLUTION_LEVEL_SETTER_EDITBOX, "Enter Evolution Level");

        // Creative Mod Tab
        add("itemGroup.blacklight_evolution_tab", "Blacklight: Evolution");
    }
}
