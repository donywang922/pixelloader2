package com.ywsuoyi.forge;

import com.ywsuoyi.PixelLoader;
import com.ywsuoyi.loadingThreadUtil.ThreadBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class PixelLoaderImpl {
    public static <T extends BlockEntity> BlockEntityType<T> buildBlockEntity(PixelLoader.Factory<T> entity, Block block) {
        return BlockEntityType.Builder.of(entity::create, block).build(null);
    }

    public static PixelLoader.Factory<ThreadBlockEntity> getThreadBlock() {
        return ThreadBlockEntityForge::new;
    }
}
