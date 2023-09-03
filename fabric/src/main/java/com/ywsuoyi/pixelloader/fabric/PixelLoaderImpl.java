package com.ywsuoyi.pixelloader.fabric;

import com.ywsuoyi.pixelloader.PixelLoader;
import com.ywsuoyi.pixelloader.loadingThreadUtil.ThreadBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;

public class PixelLoaderImpl {
    public static CreativeModeTab getTAB() {
        return PixelLoaderFabric.TAB;
    }

    public static <T extends BlockEntity> BlockEntityType<T> buildBlockEntity(PixelLoader.Factory<T> entity, Block block) {
        return FabricBlockEntityTypeBuilder.create(entity::create, block).build();
    }

    public static PixelLoader.Factory<ThreadBlockEntity> getThreadBlock() {
        return ThreadBlockEntity::new;
    }
}
