package net.rev.blacklightevolution.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.rev.blacklightevolution.languagekey.ModGuiKeys;
import net.rev.blacklightevolution.network.C2SEvolutionSingleDataPacket;

@OnlyIn(Dist.CLIENT)
public class EvolutionSetterGui extends Screen {
    EditBox editBox;
    Button doneButton;
    Button cancelButton;

    public EvolutionSetterGui(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        this.editBox = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 20, 200, 20, Component.translatable(ModGuiKeys.EVOLUTION_LEVEL_SETTER_EDITBOX));
        this.editBox.setMaxLength(10);
        this.editBox.setFilter(text -> text.matches("\\d{0,5}"));
        this.addRenderableWidget(editBox);
        this.setInitialFocus(this.editBox);

        this.doneButton = Button.builder(Component.literal("确定"), button -> {
                    String text = this.editBox.getValue();
                    if (text.isEmpty()) {
                        return;
                    }
                    int evolutionLevel = Integer.parseInt(text);
                    var player = Minecraft.getInstance().player;
                    player.connection.send(new C2SEvolutionSingleDataPacket(
                            C2SEvolutionSingleDataPacket.Operation.SET_EVOLUTION_LEVEL, evolutionLevel
                    ));
                    this.onClose();
                })
                .size(90, 20).pos(this.width / 2 - 100, this.height / 2 + 20).build();
        this.cancelButton = Button.builder(Component.literal("取消"), button -> this.onClose())
                .size(90, 20).pos(this.width / 2 + 10, this.height / 2 + 20).build();
        this.addRenderableWidget(doneButton);
        this.addRenderableWidget(cancelButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, title, this.width / 2, this.height / 2 - 40, 0xeb0505);
    }
}
