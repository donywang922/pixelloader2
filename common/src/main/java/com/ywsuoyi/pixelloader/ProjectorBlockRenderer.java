package com.ywsuoyi.pixelloader;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.ywsuoyi.pixelloader.loadingThreadUtil.ThreadBlockRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ProjectorBlockRenderer implements BlockEntityRenderer<ProjectorBlockEntity> {
    private static final RenderType projectorRenderType = RenderType.entityCutout(new ResourceLocation(PixelLoader.MOD_ID, "textures/block/projector.png"));
    public final ProjectorModel model;
    public final BlockRenderDispatcher blockRender;

    public ProjectorBlockRenderer(BlockEntityRendererProvider.Context arg) {
        blockRender = arg.getBlockRenderDispatcher();
        model = new ProjectorModel(arg.bakeLayer(ProjectorModel.projectorLayer));
    }

    @Override
    public boolean shouldRender(ProjectorBlockEntity blockEntity, Vec3 vec3) {
        return true;
    }

    @Override
    public void render(ProjectorBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        ProjectorSetting setting = ProjectorSetting.settings.get(blockEntity.getBlockPos());
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.translate(0.0, Mth.sin((blockEntity.tick + f) * 0.1f) * 0.01f, 0.0);
        if (setting != null) {
            poseStack.mulPose(Vector3f.YP.rotationDegrees((float) setting.yaw));
            poseStack.mulPose(Vector3f.XP.rotationDegrees((float) setting.pitch));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees((float) setting.roll));
            float h = 0;
            if (setting.width > 0 && setting.height > 0) {
                h = 32f / setting.width * setting.height;
            }
            model.frametop.setPos(0f, -h / 2, (float) (5.125f + setting.scale));
            model.framebot.setPos(0f, h / 2, (float) (5.125f + setting.scale));
            model.projector.visible = !setting.editing;
        }
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(projectorRenderType);
        this.model.renderToBuffer(poseStack, vertexConsumer, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();

        if (setting != null) {
            if (setting.state == ProjectorSetting.LoadState.Select)
                for (BlockPos pos : setting.outLinePos) {
                    poseStack.pushPose();
                    BlockPos pos1 = pos.subtract(blockEntity.getBlockPos());
                    poseStack.translate(pos1.getX(), pos1.getY(), pos1.getZ());
                    blockRender.renderSingleBlock(PixelLoader.outlineBlock.defaultBlockState(), poseStack, multiBufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY);
                    poseStack.popPose();
                }
            else if (setting.state == ProjectorSetting.LoadState.Start || setting.state == ProjectorSetting.LoadState.Finish) {
                ThreadBlockRenderer.renderVisualBlocks(poseStack, multiBufferSource, blockEntity.blocks, blockEntity.getBlockPos(), blockRender);
            }
        }
    }
}
