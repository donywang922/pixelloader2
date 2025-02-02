package com.ywsuoyi.forge;

import com.ywsuoyi.PixelLoader;
import com.ywsuoyi.projector.ProjectorBlockRenderer;
import com.ywsuoyi.projector.ProjectorModel;
import com.ywsuoyi.loadingThreadUtil.ThreadBlockModel;
import com.ywsuoyi.loadingThreadUtil.ThreadBlockRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import static com.ywsuoyi.PixelLoader.MOD_ID;

@Mod(MOD_ID)
public class PixelLoaderForge {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_TABS.register("tab", () -> PixelLoader.TAB);

    public PixelLoaderForge(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::init);
        modEventBus.addListener(this::register);
        modEventBus.addListener(this::RendererRegister);
        modEventBus.addListener(this::LayerRegister);
        MinecraftForge.EVENT_BUS.register(this);
        CREATIVE_TABS.register(modEventBus);
    }

    public void init(FMLCommonSetupEvent event) {
        PixelLoader.init();
    }

    public void RendererRegister(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(PixelLoader.projectorBlockEntity, ProjectorBlockRenderer::new);
        event.registerBlockEntityRenderer(PixelLoader.threadBlockEntity, ThreadBlockRenderer::new);
    }

    public void LayerRegister(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ProjectorModel.projectorLayer, ProjectorModel::createBodyLayer);
        event.registerLayerDefinition(ThreadBlockModel.threadLayer, ThreadBlockModel::createBodyLayer);
    }

    public void register(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.BLOCKS,
                Registry -> {
                    Registry.register(new ResourceLocation(MOD_ID, "traceblock"), PixelLoader.traceBlock);
                    Registry.register(new ResourceLocation(MOD_ID, "tracecenterblock"), PixelLoader.traceCenterBlock);
                    Registry.register(new ResourceLocation(MOD_ID, "projectorblock"), PixelLoader.projectorBlock);
                    Registry.register(new ResourceLocation(MOD_ID, "outlineblock"), PixelLoader.outlineBlock);
                    Registry.register(new ResourceLocation(MOD_ID, "threadblock"), PixelLoader.threadBlock);
                }
        );
        event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES,
                Registry -> {
                    Registry.register(new ResourceLocation(MOD_ID, "projectorblockentity"), PixelLoader.projectorBlockEntity);
                    Registry.register(new ResourceLocation(MOD_ID, "threadblockentity"), PixelLoader.threadBlockEntity);
                }
        );
        event.register(ForgeRegistries.Keys.ITEMS,
                Registry -> {
                    Registry.register(new ResourceLocation(MOD_ID, "colorspaceloader"), PixelLoader.coloredBlockLoader);
                    Registry.register(new ResourceLocation(MOD_ID, "imgloader"), PixelLoader.imgLoader);
                    Registry.register(new ResourceLocation(MOD_ID, "maploader"), PixelLoader.mapLoader);
                    Registry.register(new ResourceLocation(MOD_ID, "autotracer"), PixelLoader.autoTracer);
                    Registry.register(new ResourceLocation(MOD_ID, "traceblock"), PixelLoader.traceBlockItem);
                    Registry.register(new ResourceLocation(MOD_ID, "tracecenterblock"), PixelLoader.traceCenterBlockItem);
                    Registry.register(new ResourceLocation(MOD_ID, "projectorblock"), PixelLoader.projectorBlockItem);
                }
        );
    }

    @SubscribeEvent
    public void ServerStop(ServerStoppingEvent event) {
        PixelLoader.end();
    }
}
