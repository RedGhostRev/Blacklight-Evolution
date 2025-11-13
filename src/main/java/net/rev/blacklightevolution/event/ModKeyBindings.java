package net.rev.blacklightevolution.event;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.rev.blacklightevolution.languagekey.ModKeyBindingsKeys;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class ModKeyBindings {
    public static final KeyMapping GRAB_KEY = new KeyMapping(
            ModKeyBindingsKeys.GRAB,
            GLFW.GLFW_KEY_G,
            ModKeyBindingsKeys.ABILITIES
    );
    public static final KeyMapping CONSUME_KEY = new KeyMapping(
            ModKeyBindingsKeys.CONSUME,
            GLFW.GLFW_KEY_H,
            ModKeyBindingsKeys.ABILITIES
    );
}
