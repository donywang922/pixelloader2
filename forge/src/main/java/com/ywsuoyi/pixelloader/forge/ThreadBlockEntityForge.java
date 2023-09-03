package com.ywsuoyi.pixelloader.forge;

import com.ywsuoyi.pixelloader.loadingThreadUtil.ThreadBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ThreadBlockEntityForge extends ThreadBlockEntity {
    public ThreadBlockEntityForge(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}
