package com.ywsuoyi.neoforge;

import com.ywsuoyi.PixelLoader;
import com.ywsuoyi.loadingThreadUtil.ThreadBlockModel;
import com.ywsuoyi.loadingThreadUtil.ThreadBlockRenderer;
import com.ywsuoyi.projector.ProjectorBlockRenderer;
import com.ywsuoyi.projector.ProjectorModel;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(PixelLoader.MOD_ID)
public class PixelLoaderForge {


    public PixelLoaderForge(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::register);
        modEventBus.addListener(this::registerLayerDefinitions);
        modEventBus.addListener(this::registerEntityRenderers);
        NeoForge.EVENT_BUS.addListener(this::serverStop);
    }

    public void register(RegisterEvent event) {
        event.register(Registries.BLOCK,
                registry -> {
                    PixelLoaderImpl.blockRegisterHelper = registry;
                    PixelLoader.regAllBlocks();
                }
        );
        event.register(Registries.BLOCK_ENTITY_TYPE,
                registry -> {
                    PixelLoaderImpl.blockEntityRegisterHelper = registry;
                    PixelLoader.regAllBlocks();
                }
        );
        event.register(Registries.ITEM,
                registry -> {
                    PixelLoaderImpl.itemRegisterHelper = registry;
                    PixelLoader.regAllBlocks();
                }
        );
        event.register(Registries.CREATIVE_MODE_TAB,
                registry -> {
                    PixelLoaderImpl.creativeModeTabRegisterHelper = registry;
                    PixelLoader.regAllTabs();
                }
        );
    }

    public void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // Add our layer here.
        event.registerLayerDefinition(PixelLoader.projectorBlockLayer, ProjectorModel::createBodyLayer);
        event.registerLayerDefinition(PixelLoader.threadBlockLayer, ThreadBlockModel::createBodyLayer);
    }

    public void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(PixelLoader.threadBlockEntity, ThreadBlockRenderer::new);
        event.registerBlockEntityRenderer(PixelLoader.projectorBlockEntity, ProjectorBlockRenderer::new);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        PixelLoader.init();
    }

    public void serverStop(ServerStoppingEvent event) {
        PixelLoader.end();
    }
}
