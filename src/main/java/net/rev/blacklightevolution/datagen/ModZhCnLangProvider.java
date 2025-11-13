package net.rev.blacklightevolution.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.rev.blacklightevolution.item.ModItems;
import net.rev.blacklightevolution.languagekey.EvolutionKeys;
import net.rev.blacklightevolution.languagekey.ModGuiKeys;
import net.rev.blacklightevolution.languagekey.ModKeyBindingsKeys;
import net.rev.blacklightevolution.languagekey.ModMessageKeys;

public class ModZhCnLangProvider extends LanguageProvider {
    public ModZhCnLangProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        // 物品
        add(ModItems.BLACKLIGHT_VIRUS_SAMPLE.get(), "黑光病毒样本");
        add(ModItems.BLACKLIGHT_VIRUS_SERUM.get(), "黑光病毒血清");
        add(ModItems.EVOLUTION_LEVEL_SETTER.get(), "进化等级设置器");

        // 聊天框消息
        add(ModMessageKeys.INFECT, "§4你被感染了！");
        add(ModMessageKeys.INFECTED, "你已经被感染了。");
        add(ModMessageKeys.CURE, "§2你被治愈了！");
        add(ModMessageKeys.CURED, "你还很健康。");
        add(ModMessageKeys.EVOLUTION_LEVEL_SET, "你的进化等级已设置为%1$d。");
        add(ModMessageKeys.EVOLUTION_LEVEL_SET_FAIL_NOT_INFECTED, "设置失败：你还未被感染。");
        add(ModMessageKeys.EVOLUTION_LEVEL_SET_FAIL_LESS_THAN_ZERO, "设置失败：进化等级不能小于0。");

        // 进化数据文本
        // 感染状态
        add(EvolutionKeys.INFECTION_STATUS, "感染状态");
        add(EvolutionKeys.INFECTION_STATUS_TRUE, "已感染");
        add(EvolutionKeys.INFECTION_STATUS_FALSE, "未感染");
        // 进化等级
        add(EvolutionKeys.EVOLUTION_LEVEL, "进化等级");
        // 进化阶段
        add(EvolutionKeys.EVOLUTION_STAGE, "进化阶段");
        add(EvolutionKeys.EVOLUTION_STAGE_HUMAN, "凡人");
        add(EvolutionKeys.EVOLUTION_STAGE_INFILTRATOR, "渗透者");
        add(EvolutionKeys.EVOLUTION_STAGE_INVADER, "侵袭者");
        add(EvolutionKeys.EVOLUTION_STAGE_SLAYER, "猎杀者");
        add(EvolutionKeys.EVOLUTION_STAGE_REAPER, "收割者");
        add(EvolutionKeys.EVOLUTION_STAGE_ANNIHILATOR, "湮灭者");
        add(EvolutionKeys.EVOLUTION_STAGE_ULTIMATE_PROTOTYPE, "终极原型体");

        // 按键文本
        // 按键类型
        add(ModKeyBindingsKeys.ABILITIES, "黑光：进化 - 能力");
        // 按键
        add(ModKeyBindingsKeys.GRAB, "抓取");
        add(ModKeyBindingsKeys.CONSUME, "吞噬");

        // GUI文本
        // 进化等级设置器
        add(ModGuiKeys.EVOLUTION_LEVEL_SETTER_TITLE, "进化等级设置");
        add(ModGuiKeys.EVOLUTION_LEVEL_SETTER_EDITBOX, "输入进化等级");

        // 创造模式物品栏
        add("itemGroup.blacklight_evolution_tab", "黑光：进化");
    }
}
