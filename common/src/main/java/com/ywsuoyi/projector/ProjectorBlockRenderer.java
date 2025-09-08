package com.ywsuoyi.projector;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ywsuoyi.PixelLoader;
import com.ywsuoyi.loadingThreadUtil.CachedQuadData;
import com.ywsuoyi.loadingThreadUtil.ThreadBlockRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4d;
import org.joml.Quaternionf;

public class ProjectorBlockRenderer implements BlockEntityRenderer<ProjectorBlockEntity> {
    public final ModelPart model;
    public final BlockRenderDispatcher blockRender;

    public ProjectorBlockRenderer(BlockEntityRendererProvider.Context context) {
        blockRender = context.getBlockRenderDispatcher();
        model = context.bakeLayer(PixelLoader.projectorBlockLayer);
    }

    @Override
    public boolean shouldRender(ProjectorBlockEntity blockEntity, Vec3 vec3) {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen(ProjectorBlockEntity blockEntity) {
        return true;
    }

    @Override
    public void render(ProjectorBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        ProjectorSetting setting = ProjectorSetting.settings.get(blockEntity.getBlockPos());
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.translate(0.0, Mth.sin((blockEntity.tick + f) * 0.1f) * 0.01f, 0.0);
        if (setting != null) {
            poseStack.mulPose(new Quaternionf(new AxisAngle4d()));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) setting.yaw));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) setting.pitch));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) setting.roll));
            float h = 0;
            if (setting.width > 0 && setting.height > 0) {
                h = 32f / setting.width * setting.height;
            }
            model.getChild("frametop").setPos(0f, -h / 2, (float) (5.125f + setting.scale));
            model.getChild("framebot").setPos(0f, h / 2, (float) (5.125f + setting.scale));
            model.getChild("projector").visible = !setting.editing;
        }
        model.render(poseStack, PixelLoader.projectorBlockMaterial.buffer(multiBufferSource, RenderType::entityCutout), i, j);
        poseStack.popPose();

        if (setting != null) {
            if (setting.state == ProjectorSetting.LoadState.Select)
                for (BlockPos pos : setting.latticePos) {
                    if (pos == null) continue;
                    poseStack.pushPose();
                    BlockPos pos1 = pos.subtract(blockEntity.getBlockPos());
                    poseStack.translate(pos1.getX(), pos1.getY(), pos1.getZ());
                    blockRender.renderSingleBlock(PixelLoader.outlineBlock.defaultBlockState(), poseStack, multiBufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY);
                    poseStack.popPose();
                }
            else if (setting.state == ProjectorSetting.LoadState.Finish) {
                if (setting.cachedQuads == null)
                    setting.cachedQuads = CachedQuadData.build(setting.genBlocks, blockRender);
                poseStack.pushPose();
                poseStack.translate(-blockEntity.getBlockPos().getX(), -blockEntity.getBlockPos().getY(), -blockEntity.getBlockPos().getZ());
                ThreadBlockRenderer.renderVisualBlocks(poseStack, multiBufferSource, setting.cachedQuads, 1);
                poseStack.popPose();
            }
        }
    }
}
