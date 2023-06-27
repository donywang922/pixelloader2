package com.ywsuoyi.pixelloader;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ProjectorBlockRenderer implements BlockEntityRenderer<ProjectorBlockEntity> {

    public ProjectorBlockRenderer(BlockEntityRendererProvider.Context arg) {

    }

    @Override
    public void render(ProjectorBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        ProjectorSetting projectorSetting = ProjectorSetting.settings.get(blockEntity.getBlockPos());

    }
}
