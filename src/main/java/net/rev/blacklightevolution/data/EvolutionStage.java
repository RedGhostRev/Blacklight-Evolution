package net.rev.blacklightevolution.data;

public enum EvolutionStage {
    // 凡人
    HUMAN,
    // 渗透者
    INFILTRATOR,
    // 侵袭者
    INVADER,
    // 猎杀者
    SLAYER,
    // 收割者
    REAPER,
    // 湮灭者
    ANNIHILATOR,
    // 终极原型体
    ULTIMATE_PROTOTYPE;

    public static EvolutionStage from(boolean infected, int evolutionLevel) {
        if (!infected) {
            return HUMAN;
        }
        if (evolutionLevel <= 9) {
            return INFILTRATOR;
        } else if (evolutionLevel <= 29) {
            return INVADER;
        } else if (evolutionLevel <= 49) {
            return SLAYER;
        } else if (evolutionLevel <= 79) {
            return REAPER;
        } else if (evolutionLevel <= 99) {
            return ANNIHILATOR;
        } else {
            return ULTIMATE_PROTOTYPE;
        }
    }
}