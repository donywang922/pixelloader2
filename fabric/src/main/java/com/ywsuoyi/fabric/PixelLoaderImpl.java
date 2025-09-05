package com.ywsuoyi.fabric;

import com.ywsuoyi.PixelLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class PixelLoaderImpl {
    public static <T extends BlockEntity> BlockEntityType<T> buildBlockEntity(PixelLoader.Factory<T> entity, Block block) {
        return BlockEntityType.Builder.of(entity::create, block).build(null);
    }

    public static Item regItem(String name, Item item) {
        Registry.register(BuiltInRegistries.ITEM, PixelLoader.loc(name), item);
        return item;
    }

    public static <T extends Block> T regBlock(String name, T block) {
        Registry.register(BuiltInRegistries.BLOCK, PixelLoader.loc(name), block);
        return block;
    }

    public static <T extends BlockEntity> BlockEntityType<T> regBlockEntity(String name, BlockEntityType<T> blockEntity) {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, PixelLoader.loc(name), blockEntity);
        return blockEntity;
    }

    public static CreativeModeTab regTab(String name, CreativeModeTab tab) {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, PixelLoader.loc(name), tab);
        return tab;
    }
}
