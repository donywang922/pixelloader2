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
        PixelLoader.regAllBlocks();
        PixelLoader.regAllBlockEntity();
        PixelLoader.regAllItems();
        PixelLoader.regAllTabs();
        PixelLoader.init();
        ServerWorldEvents.UNLOAD.register((server, level) -> PixelLoader.end());
    }
}
