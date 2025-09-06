package com.ywsuoyi.loadingThreadUtil;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ywsuoyi.PixelLoader;
import net.minecraft.Util;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

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
            BlockPos caOffset = data.center.subtract(blockEntity.getBlockPos());
            poseStack.pushPose();
            poseStack.translate(caOffset.getX(), caOffset.getY(), caOffset.getZ());
            long tm = Util.getMillis();
            renderVisualBlocks(poseStack, multiBufferSource, data.genBlocks, blockRender, data.directions, data.renderPercentage);
            if (data.autoLowerPercentage && Util.getMillis() - tm > 200) {
                data.renderPercentage = 0;
                data.autoLowerPercentage = false;
            }
            poseStack.popPose();
        }

    }


    public static void renderVisualBlocks(PoseStack poseStack, MultiBufferSource multiBufferSource, List<Tuple<BlockPos, BlockState>> blocks, BlockRenderDispatcher blockRender, Direction[] directions, float percentage) {
        if (percentage == 0) return;
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.solid());
        RandomSource randomSource = RandomSource.create(42);
        float counter = 0.001f;
        for (Tuple<BlockPos, BlockState> tuple : blocks) {
            counter -= percentage;
            if (counter > 0) continue;
            counter += 1;
            poseStack.pushPose();
            BlockPos pos1 = tuple.getA();
            poseStack.translate(pos1.getX(), pos1.getY(), pos1.getZ());
            BakedModel blockModel = blockRender.getBlockModel(tuple.getB());
            for (Direction direction : directions) {
                for (BakedQuad quad : blockModel.getQuads(tuple.getB(), direction, randomSource)) {
                    vertexConsumer.putBulkData(poseStack.last(), quad, 1, 1, 1, 1, 0xF000F0, OverlayTexture.NO_OVERLAY);
                }
            }
            poseStack.popPose();
        }
    }
}
