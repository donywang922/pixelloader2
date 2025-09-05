package com.ywsuoyi.neoforge;

import com.ywsuoyi.PixelLoader;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.RegisterEvent;

public class PixelLoaderImpl {
    public static <T extends BlockEntity> BlockEntityType<T> buildBlockEntity(PixelLoader.Factory<T> entity, Block block) {
        return BlockEntityType.Builder.of(entity::create, block).build(null);
    }

    public static RegisterEvent.RegisterHelper<Item> itemRegisterHelper;

    public static Item regItem(String name, Item item) {
        itemRegisterHelper.register(PixelLoader.loc(name), item);
        return item;
    }

    public static RegisterEvent.RegisterHelper<Block> blockRegisterHelper;

    public static <T extends Block> T regBlock(String name, T block) {
        blockRegisterHelper.register(PixelLoader.loc(name), block);
        return block;
    }

    public static RegisterEvent.RegisterHelper<BlockEntityType<?>> blockEntityRegisterHelper;

    public static <T extends BlockEntity> BlockEntityType<T> regBlockEntity(String name, BlockEntityType<T> blockEntity) {
        blockEntityRegisterHelper.register(PixelLoader.loc(name), blockEntity);
        return blockEntity;
    }

    public static RegisterEvent.RegisterHelper<CreativeModeTab> creativeModeTabRegisterHelper;
    public static CreativeModeTab regTab(String name, CreativeModeTab tab) {
        creativeModeTabRegisterHelper.register(PixelLoader.loc(name), tab);
        return tab;
    }
}
