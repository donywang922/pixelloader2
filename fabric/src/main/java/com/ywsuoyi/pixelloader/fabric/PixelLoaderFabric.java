package com.ywsuoyi.pixelloader.fabric;

import com.ywsuoyi.pixelloader.PixelLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class PixelLoaderFabric implements ModInitializer {
    protected static final CreativeModeTab TAB = FabricItemGroupBuilder.create(new ResourceLocation(PixelLoader.MOD_ID, "pixelloader"))
            .icon(() -> new ItemStack(PixelLoader.coloredBlockLoader))
            .build();


    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new ResourceLocation(PixelLoader.MOD_ID, "colorspaceloader"), PixelLoader.coloredBlockLoader);
        Registry.register(Registry.ITEM, new ResourceLocation(PixelLoader.MOD_ID, "imgloader"), PixelLoader.imgLoader);
        Registry.register(Registry.ITEM, new ResourceLocation(PixelLoader.MOD_ID, "maploader"), PixelLoader.mapLoader);
        Registry.register(Registry.ITEM, new ResourceLocation(PixelLoader.MOD_ID, "autotracer"), PixelLoader.autoTracer);

        Registry.register(Registry.BLOCK, new ResourceLocation(PixelLoader.MOD_ID, "threadblock"), PixelLoader.threadBlock);
        Registry.register(Registry.BLOCK, new ResourceLocation(PixelLoader.MOD_ID, "outlineblock"), PixelLoader.outlineBlock);

        Registry.register(Registry.BLOCK, new ResourceLocation(PixelLoader.MOD_ID, "traceblock"), PixelLoader.traceBlock);
        Registry.register(Registry.ITEM, new ResourceLocation(PixelLoader.MOD_ID, "traceblock"), PixelLoader.traceBlockItem);

        Registry.register(Registry.BLOCK, new ResourceLocation(PixelLoader.MOD_ID, "tracecenterblock"), PixelLoader.traceCenterBlock);
        Registry.register(Registry.ITEM, new ResourceLocation(PixelLoader.MOD_ID, "tracecenterblock"), PixelLoader.traceCenterBlockItem);

        Registry.register(Registry.BLOCK, new ResourceLocation(PixelLoader.MOD_ID, "projectorblock"), PixelLoader.projectorBlock);
        Registry.register(Registry.ITEM, new ResourceLocation(PixelLoader.MOD_ID, "projectorblock"), PixelLoader.traceCenterBlockItem);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(PixelLoader.MOD_ID, "projectorblockentity"), PixelLoader.projectorBlockEntity);
        PixelLoader.init();
        ServerWorldEvents.UNLOAD.register((server, level) -> PixelLoader.end());
    }

}
