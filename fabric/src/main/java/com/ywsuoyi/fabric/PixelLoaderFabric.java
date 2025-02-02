package com.ywsuoyi.fabric;

import com.ywsuoyi.PixelLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class PixelLoaderFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation(PixelLoader.MOD_ID, "tab"), PixelLoader.TAB);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(PixelLoader.MOD_ID, "colorspaceloader"), PixelLoader.coloredBlockLoader);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(PixelLoader.MOD_ID, "imgloader"), PixelLoader.imgLoader);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(PixelLoader.MOD_ID, "maploader"), PixelLoader.mapLoader);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(PixelLoader.MOD_ID, "autotracer"), PixelLoader.autoTracer);


        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(PixelLoader.MOD_ID, "outlineblock"), PixelLoader.outlineBlock);

        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(PixelLoader.MOD_ID, "traceblock"), PixelLoader.traceBlock);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(PixelLoader.MOD_ID, "traceblock"), PixelLoader.traceBlockItem);

        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(PixelLoader.MOD_ID, "tracecenterblock"), PixelLoader.traceCenterBlock);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(PixelLoader.MOD_ID, "tracecenterblock"), PixelLoader.traceCenterBlockItem);

        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(PixelLoader.MOD_ID, "projectorblock"), PixelLoader.projectorBlock);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(PixelLoader.MOD_ID, "projectorblock"), PixelLoader.projectorBlockItem);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(PixelLoader.MOD_ID, "projectorblockentity"), PixelLoader.projectorBlockEntity);

        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(PixelLoader.MOD_ID, "threadblock"), PixelLoader.threadBlock);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(PixelLoader.MOD_ID, "threadblockentity"), PixelLoader.threadBlockEntity);

        PixelLoader.init();
        ServerWorldEvents.UNLOAD.register((server, level) -> PixelLoader.end());
    }

}
