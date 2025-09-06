package com.ywsuoyi.neoforge;

import com.ywsuoyi.PixelLoader;
import com.ywsuoyi.loadingThreadUtil.ThreadBlockModel;
import com.ywsuoyi.projector.ProjectorModel;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(PixelLoader.MOD_ID)
@EventBusSubscriber(modid = PixelLoader.MOD_ID)
public class PixelLoaderForge {
    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.BLOCK,
                registry -> {
                    PixelLoaderImpl.blockRegisterHelper = registry;
                    PixelLoader.regAllBlocks();
                }
        );
        event.register(Registries.BLOCK_ENTITY_TYPE,
                registry -> {
                    PixelLoaderImpl.blockEntityRegisterHelper = registry;
                    PixelLoader.regAllBlockEntity();
                }
        );
        event.register(Registries.ITEM,
                registry -> {
                    PixelLoaderImpl.itemRegisterHelper = registry;
                    PixelLoader.regAllItems();
                }
        );
        event.register(Registries.CREATIVE_MODE_TAB,
                registry -> {
                    PixelLoaderImpl.creativeModeTabRegisterHelper = registry;
                    PixelLoader.regAllTabs();
                }
        );
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        PixelLoader.init();
    }

    @SubscribeEvent
    public static void serverStop(ServerStoppingEvent event) {
        PixelLoader.end();
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // Add our layer here.
        event.registerLayerDefinition(PixelLoader.projectorBlockLayer, ProjectorModel::createBodyLayer);
        event.registerLayerDefinition(PixelLoader.threadBlockLayer, ThreadBlockModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(PixelLoader.threadBlockEntity, ThreadBlockRendererForge::new);
        event.registerBlockEntityRenderer(PixelLoader.projectorBlockEntity, ProjectorBlockRendererForge::new);
    }
}
