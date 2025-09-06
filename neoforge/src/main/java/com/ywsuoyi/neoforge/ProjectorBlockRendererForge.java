package com.ywsuoyi.neoforge;

import com.ywsuoyi.projector.ProjectorBlockEntity;
import com.ywsuoyi.projector.ProjectorBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class ProjectorBlockRendererForge extends ProjectorBlockRenderer {
    public ProjectorBlockRendererForge(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull ProjectorBlockEntity blockEntity) {
        return AABB.INFINITE;
    }
}
