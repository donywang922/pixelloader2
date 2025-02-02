package com.ywsuoyi.fabric;

import com.ywsuoyi.PixelLoader;
import com.ywsuoyi.loadingThreadUtil.ThreadBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class PixelLoaderImpl {

    public static <T extends BlockEntity> BlockEntityType<T> buildBlockEntity(PixelLoader.Factory<T> entity, Block block) {
        return FabricBlockEntityTypeBuilder.create(entity::create, block).build();
    }

    public static PixelLoader.Factory<ThreadBlockEntity> getThreadBlock() {
        return ThreadBlockEntity::new;
    }
}
