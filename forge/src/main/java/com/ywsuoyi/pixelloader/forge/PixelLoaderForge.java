package com.ywsuoyi.pixelloader.forge;

import com.ywsuoyi.pixelloader.PixelLoader;
import com.ywsuoyi.pixelloader.ProjectorBlockRenderer;
import com.ywsuoyi.pixelloader.ProjectorModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

@Mod(PixelLoader.MOD_ID)
public class PixelLoaderForge {
    public static final CreativeModeTab TAB = new CreativeModeTab(PixelLoader.MOD_ID + ".pixelloader") {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(PixelLoader.coloredBlockLoader);
        }
    };

    public PixelLoaderForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::init);
        modEventBus.addListener(this::register);
        modEventBus.addListener(this::RendererRegister);
        modEventBus.addListener(this::LayerRegister);
        MinecraftForge.EVENT_BUS.register(this);

    }

    public void init(FMLCommonSetupEvent event) {
        PixelLoader.init();
    }

    public void RendererRegister(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(PixelLoader.projectorBlockEntity, ProjectorBlockRenderer::new);
    }

    public void LayerRegister(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ProjectorModel.projectorLayer, ProjectorModel::createBodyLayer);
    }

    public void register(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.BLOCKS,
                Registry -> {
                    Registry.register(new ResourceLocation(PixelLoader.MOD_ID, "threadblock"), PixelLoader.threadBlock);
                    Registry.register(new ResourceLocation(PixelLoader.MOD_ID, "traceblock"), PixelLoader.traceBlock);
                    Registry.register(new ResourceLocation(PixelLoader.MOD_ID, "tracecenterblock"), PixelLoader.traceCenterBlock);
                    Registry.register(new ResourceLocation(PixelLoader.MOD_ID, "projectorblock"), PixelLoader.projectorBlock);
                    Registry.register(new ResourceLocation(PixelLoader.MOD_ID, "outlineblock"), PixelLoader.outlineBlock);
                }
        );
        event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES,
                Registry -> {
                    Registry.register(new ResourceLocation(PixelLoader.MOD_ID, "projectorblockentity"), PixelLoader.projectorBlockEntity);
                }
        );
        event.register(ForgeRegistries.Keys.ITEMS,
                Registry -> {
                    Registry.register(new ResourceLocation(PixelLoader.MOD_ID, "coloredblockloader"), PixelLoader.coloredBlockLoader);
                    Registry.register(new ResourceLocation(PixelLoader.MOD_ID, "imgloader"), PixelLoader.imgLoader);
                    Registry.register(new ResourceLocation(PixelLoader.MOD_ID, "maploader"), PixelLoader.mapLoader);
                    Registry.register(new ResourceLocation(PixelLoader.MOD_ID, "autotracer"), PixelLoader.autoTracer);
                    Registry.register(new ResourceLocation(PixelLoader.MOD_ID, "traceblock"), PixelLoader.traceBlockItem);
                    Registry.register(new ResourceLocation(PixelLoader.MOD_ID, "tracecenterblock"), PixelLoader.traceCenterBlockItem);
                    Registry.register(new ResourceLocation(PixelLoader.MOD_ID, "projectorblock"), PixelLoader.projectorBlockItem);
                }
        );
    }

    @SubscribeEvent
    public void ServerStop(ServerStoppingEvent event) {
        PixelLoader.end();
    }
}
