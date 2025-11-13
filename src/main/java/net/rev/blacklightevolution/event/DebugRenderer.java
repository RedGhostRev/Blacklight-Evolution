package net.rev.blacklightevolution.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import org.joml.Matrix4f;

public class DebugRenderer {
    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Post<LivingEntity, ?> event) {
        LivingEntity entity = event.getEntity();
        if (Minecraft.getInstance().player == null) {
            return;
        }
        if (!(entity instanceof Creeper)) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();
        Font font = Minecraft.getInstance().font;

        Component text = Component.literal("TEST");
        float textWidth = font.width(text);

        float scale = 0.05F;
        float yOffset = entity.getBbHeight() + 0.5F;


        poseStack.pushPose();
        poseStack.translate(0, yOffset, 0);
        float cameraYaw = Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
        float cameraPitch = Minecraft.getInstance().gameRenderer.getMainCamera().getXRot();
        poseStack.scale(-scale, -scale, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(cameraYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-cameraPitch));
        Matrix4f modelViewMatrix = poseStack.last().pose();

        float x = -textWidth / 2;
        float y = 0;
        int color = 0xFFFFFFFF;

        font.drawInBatch(text, x, y, color, false, modelViewMatrix, bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0);
        poseStack.popPose();
    }
}
