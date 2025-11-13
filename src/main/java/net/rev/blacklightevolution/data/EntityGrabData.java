package net.rev.blacklightevolution.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Optional;
import java.util.UUID;

public class EntityGrabData implements INBTSerializable<CompoundTag> {
    private UUID grabberPlayerUUID;

    public boolean isGrabbed() {
        return grabberPlayerUUID != null;
    }

    public Optional<UUID> getGrabberPlayerUUID() {
        return Optional.ofNullable(grabberPlayerUUID);
    }

    public void setGrabberPlayerUUID(UUID grabberPlayerUUID) {
        this.grabberPlayerUUID = grabberPlayerUUID;
    }

    public void clearGrabberPlayerUUID() {
        this.grabberPlayerUUID = null;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        if (grabberPlayerUUID != null) {
            tag.putUUID("grabberPlayer", grabberPlayerUUID);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.hasUUID("grabberPlayer")) {
            grabberPlayerUUID = tag.getUUID("grabberPlayer");
        }
    }
}
