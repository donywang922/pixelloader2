package com.ywsuoyi.loadingThreadUtil;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ywsuoyi.PixelLoader;
import net.minecraft.Util;
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

public class ThreadBlockRenderer implements BlockEntityRenderer<ThreadBlockEntity> {

    public static float[] ofst = new float[]{0, 2.7475f, 1.1775f, 0.785f, 1.9625f, 1.57f, 2.355f, 0.3925f};

    public final BlockRenderDispatcher blockRender;
    public final ModelPart model;

    public ThreadBlockRenderer(BlockEntityRendererProvider.Context context) {
        blockRender = context.getBlockRenderDispatcher();
        model = context.bakeLayer(PixelLoader.threadBlockLayer);
    }

    @Override
    public boolean shouldRender(ThreadBlockEntity blockEntity, Vec3 vec3) {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen(ThreadBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public void render(ThreadBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        for (int l = 0; l < 8; l++) {
            model.getChild("l" + (l + 1)).setPos(0, Mth.sin((blockEntity.tick + f) * 0.1f + ofst[l] * 2) * 4, 0);
        }
        model.getChild("center").yRot = (blockEntity.tick + f) * 0.01f;
        model.render(poseStack, PixelLoader.threadBlockMaterial.buffer(multiBufferSource, RenderType::entityCutout), i, j);
        poseStack.popPose();

        ThreadData data = ThreadData.data.get(blockEntity.getBlockPos());

        if (data != null && data.thread.state == BaseThread.State.end) {
            if (data.cachedQuads == null) {
                data.cachedQuads = CachedQuadData.build(data.genBlocks, blockRender);
            }
            BlockPos caOffset = data.center.subtract(blockEntity.getBlockPos());
            poseStack.pushPose();
            poseStack.translate(caOffset.getX(), caOffset.getY(), caOffset.getZ());

            long tm = Util.getMillis();
            renderVisualBlocks(poseStack, multiBufferSource, data.cachedQuads, data.renderPercentage);
            if (data.autoLowerPercentage && Util.getMillis() - tm > 200) {
                data.renderPercentage = 0;
                data.autoLowerPercentage = false;
            }
            poseStack.popPose();
        }
    }

    public static void renderVisualBlocks(PoseStack poseStack, MultiBufferSource multiBufferSource, CachedQuadData quads, float percentage) {
        if (percentage == 0) return;
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.solid());
        float counter = 0.001f;
        for (CachedQuadData.CachedQuad quad : quads.quads()) {
            counter -= percentage;
            if (counter > 0) continue;
            counter += 1;
            poseStack.pushPose();
            BlockPos pos1 = quad.pos();
            poseStack.translate(pos1.getX(), pos1.getY(), pos1.getZ());
            vertexConsumer.putBulkData(poseStack.last(), quad.quad(), 1, 1, 1, 1, 0xF000F0, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }
}
