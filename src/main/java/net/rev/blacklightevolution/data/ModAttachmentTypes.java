package net.rev.blacklightevolution.data;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.rev.blacklightevolution.BlacklightEvolution;

import java.util.function.Supplier;

public class ModAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, BlacklightEvolution.MOD_ID);

    public static final Supplier<AttachmentType<EvolutionData>> EVOLUTION_DATA =
            ATTACHMENT_TYPES.register("evolution_data",
                    () -> AttachmentType.serializable(EvolutionData::new).build());

    public static final Supplier<AttachmentType<EntityGrabData>> ENTITY_GRAB_DATA =
            ATTACHMENT_TYPES.register("entity_grab_data",
                    () -> AttachmentType.serializable(EntityGrabData::new).build());

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
