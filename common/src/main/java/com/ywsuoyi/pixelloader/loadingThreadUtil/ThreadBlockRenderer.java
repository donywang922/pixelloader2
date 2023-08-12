package com.ywsuoyi.pixelloader.loadingThreadUtil;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ywsuoyi.pixelloader.PixelLoader;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;

public class ThreadBlockRenderer implements BlockEntityRenderer<ThreadBlockEntity> {

    public static float[] ofst = new float[]{0, 2.7475f, 1.1775f, 0.785f, 1.9625f, 1.57f, 2.355f, 0.3925f};
    private static final RenderType threadBlockRenderType = RenderType.entityCutout(new ResourceLocation(PixelLoader.MOD_ID, "textures/block/threadblock.png"));

    public final BlockRenderDispatcher blockRender;
    public final ThreadBlockModel model;

    public ThreadBlockRenderer(BlockEntityRendererProvider.Context arg) {
        blockRender = arg.getBlockRenderDispatcher();
        model = new ThreadBlockModel(arg.bakeLayer(ThreadBlockModel.threadLayer));
    }

    @Override
    public boolean shouldRender(ThreadBlockEntity blockEntity, Vec3 vec3) {
        return true;
    }

    @Override
    public void render(ThreadBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(threadBlockRenderType);
        for (int l = 0; l < 8; l++) {
            model.modelParts[l + 1].setPos(0, Mth.sin((blockEntity.tick + f) * 0.1f + ofst[l] * 2) * 4, 0);
        }
        model.center.setRotation(0, (blockEntity.tick + f) * 0.01f, 0);

        this.model.renderToBuffer(poseStack, vertexConsumer, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
        renderVisualBlocks(poseStack, multiBufferSource, blockEntity.blocks, blockEntity.getBlockPos(), blockRender);
    }


    public static void renderVisualBlocks(PoseStack poseStack, MultiBufferSource multiBufferSource, HashMap<BlockPos, BlockState> blocks, BlockPos blockPos, BlockRenderDispatcher blockRender) {
        blocks.forEach((pos, state) -> {
            poseStack.pushPose();
            BlockPos pos1 = pos.subtract(blockPos);
            poseStack.translate(pos1.getX(), pos1.getY(), pos1.getZ());
            poseStack.scale(1.001f, 1.001f, 1.001f);
            blockRender.renderSingleBlock(state, poseStack, multiBufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        });
    }
}
