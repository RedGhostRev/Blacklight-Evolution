package net.rev.blacklightevolution.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class EvolutionData implements INBTSerializable<CompoundTag> {
    private boolean infected = false;
    private int evolutionLevel = 0;
    private final GrabData grabData = new GrabData();

    public boolean isInfected() {
        return this.infected;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }

    public int getEvolutionLevel() {
        return this.evolutionLevel;
    }

    public void setEvolutionLevel(int evolutionLevel) {
        this.evolutionLevel = evolutionLevel;
    }

    public GrabData getGrabData() {
        return this.grabData;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("infected", this.infected);
        tag.putInt("evolutionLevel", this.evolutionLevel);
        tag.put("grabData", this.grabData.serializeNBT(provider));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.infected = tag.getBoolean("infected");
        this.evolutionLevel = tag.getInt("evolutionLevel");
        if (tag.contains("grabData")) {
            this.grabData.deserializeNBT(provider, tag.getCompound("grabData"));
        }
    }
}
