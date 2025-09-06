package com.ywsuoyi.neoforge;

import com.ywsuoyi.loadingThreadUtil.ThreadBlockEntity;
import com.ywsuoyi.loadingThreadUtil.ThreadBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class ThreadBlockRendererForge extends ThreadBlockRenderer {

    public ThreadBlockRendererForge(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull ThreadBlockEntity blockEntity) {
        return AABB.INFINITE;
    }
}
