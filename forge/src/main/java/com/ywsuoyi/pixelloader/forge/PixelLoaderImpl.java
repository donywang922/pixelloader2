package com.ywsuoyi.pixelloader.forge;

import com.ywsuoyi.pixelloader.PixelLoader;
import com.ywsuoyi.pixelloader.loadingThreadUtil.ThreadBlockEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;

public class PixelLoaderImpl {
    public static CreativeModeTab getTAB() {
        return PixelLoaderForge.TAB;
    }

    public static <T extends BlockEntity> BlockEntityType<T> buildBlockEntity(PixelLoader.Factory<T> entity, Block block) {
        return BlockEntityType.Builder.of(entity::create, block).build(null);
    }

    public static PixelLoader.Factory<ThreadBlockEntity> getThreadBlock() {
        return ThreadBlockEntityForge::new;
    }
}
