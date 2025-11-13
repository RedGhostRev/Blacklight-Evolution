package net.rev.blacklightevolution.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Optional;
import java.util.UUID;

public class GrabData implements INBTSerializable<CompoundTag> {
    private UUID grabbedEntityUUID;

    public boolean isGrabbing() {
        return grabbedEntityUUID != null;
    }

    public Optional<UUID> getGrabbedEntityUUID() {
        return Optional.ofNullable(grabbedEntityUUID);
    }

    public void setGrabbedEntityUUID(UUID grabbedEntityUUID) {
        this.grabbedEntityUUID = grabbedEntityUUID;
    }

    public void clearGrabbedEntityUUID() {
        this.grabbedEntityUUID = null;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        if (grabbedEntityUUID != null) {
            tag.putUUID("grabbedEntity", grabbedEntityUUID);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.hasUUID("grabbedEntity")) {
            grabbedEntityUUID = tag.getUUID("grabbedEntity");
        }
    }
}
