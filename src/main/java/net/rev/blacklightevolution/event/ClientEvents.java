package net.rev.blacklightevolution.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.rev.blacklightevolution.BlacklightEvolution;
import net.rev.blacklightevolution.data.GrabData;
import net.rev.blacklightevolution.handler.GrabHandler;
import net.rev.blacklightevolution.manager.EvolutionManager;
import net.rev.blacklightevolution.network.C2SConsumeEntityPacket;
import net.rev.blacklightevolution.network.C2SGrabEntityPacket;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

@EventBusSubscriber(modid = BlacklightEvolution.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    // 注册按键
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ModKeyBindings.GRAB_KEY);
        event.register(ModKeyBindings.CONSUME_KEY);
    }

    // 按键监听
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == ModKeyBindings.GRAB_KEY.getKey().getValue()) {
            // 抓取
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            GrabData grabData = EvolutionManager.getInstance().getGrabData(player);
            if (grabData.isGrabbing()) {
                player.connection.send(new C2SGrabEntityPacket(Optional.empty()));
                return;
            }
            if (minecraft.hitResult instanceof EntityHitResult entityHit) {
                Entity target = entityHit.getEntity();
                if (!GrabHandler.canGrab(player, target)) {
                    return;
                }
                player.connection.send(new C2SGrabEntityPacket(Optional.of(target.getUUID())));
            }
        }
        if (event.getAction() == GLFW.GLFW_PRESS && event.getKey() == ModKeyBindings.CONSUME_KEY.getKey().getValue()) {
            // 吞噬
            LocalPlayer player = Minecraft.getInstance().player;
            GrabData grabData = EvolutionManager.getInstance().getGrabData(player);
            if (grabData.isGrabbing()) {
                grabData.getGrabbedEntityUUID().ifPresent(uuid -> player.connection.send(new C2SConsumeEntityPacket(uuid)));
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event) {
        Player player = Minecraft.getInstance().player;
        LivingEntity entity = event.getEntity();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();
        renderHealth(entity, poseStack, bufferSource);
        if (player == null) {
            return;
        }
        if (!GrabHandler.canGrab(player, entity)) {
            return;
        }
        renderGrabbableMarker(player, entity, poseStack, bufferSource);
    }

    // 渲染生命值用以测试
    private static void renderHealth(LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferSource) {
        if (entity instanceof ArmorStand) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || entity == minecraft.player) {
            return;
        }
        if (!minecraft.player.hasLineOfSight(entity)) {
            return;
        }
        if (entity.position().distanceTo(minecraft.player.position()) > 16.0) {
            return;
        }
        Font font = minecraft.font;
        Component health = Component.literal(String.format("%.1f / %.1f", entity.getHealth(), entity.getMaxHealth()));
        float scale = 0.02F;
        float yOffset = entity.getEyeHeight();
        Vec3 camPos = minecraft.gameRenderer.getMainCamera().getPosition();
        Vec3 labelWorldPos = entity.position().add(0.0, yOffset, 0.0);
        HitResult hit = entity.level().clip(new ClipContext(
                camPos, labelWorldPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                minecraft.player
        ));
        boolean labelBlockedByBlock = hit.getType() == HitResult.Type.BLOCK;
        poseStack.pushPose();
        poseStack.translate(0, yOffset + font.lineHeight / 2.0 * scale, 0);
        float cameraYaw = minecraft.gameRenderer.getMainCamera().getYRot();
        float cameraPitch = minecraft.gameRenderer.getMainCamera().getXRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(-cameraYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(cameraPitch));
        poseStack.translate(-entity.getBbWidth(), 0, 0);
        poseStack.scale(-scale, -scale, scale);
        Matrix4f matrix = poseStack.last().pose();
        float x = -entity.getBbWidth() / 2;
        float y = 0;
        int color = 0xFFFFFFFF;
        int bg = (int) (minecraft.options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
        if (!labelBlockedByBlock) {
            font.drawInBatch(health, x, y, color, false, matrix, bufferSource, Font.DisplayMode.SEE_THROUGH, bg, 15728880);
        }
        font.drawInBatch(health, x, y, color, false, matrix, bufferSource, Font.DisplayMode.POLYGON_OFFSET, 0, 15728880);
        poseStack.popPose();
    }

    // 渲染可抓取标识
    private static void renderGrabbableMarker(Player player, LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        if (entity.position().distanceTo(player.position()) > 16.0) {
            return;
        }
        Font font = minecraft.font;
        Component marker = Component.literal("\\|/");
        float textWidth = font.width(marker);
        float scale = 0.03F;
        float yOffset = entity.getBbHeight() + 0.5F;
        poseStack.pushPose();
        poseStack.translate(0, yOffset, 0);
        float cameraYaw = minecraft.gameRenderer.getMainCamera().getYRot();
        float cameraPitch = minecraft.gameRenderer.getMainCamera().getXRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(-cameraYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(cameraPitch));
        poseStack.scale(-scale, -scale, scale);
        Matrix4f matrix = poseStack.last().pose();
        float x = -textWidth / 2;
        float y = 0;
        int color = 0xFFFFFFFF;
        font.drawInBatch(marker, x, y, color, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0);
        poseStack.popPose();
    }
}
