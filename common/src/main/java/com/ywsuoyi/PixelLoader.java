package com.ywsuoyi;

import com.mojang.logging.LogUtils;
import com.ywsuoyi.colorspace.ColorSpace;
import com.ywsuoyi.colorspace.ColorSpaceLoader;
import com.ywsuoyi.imgLoader.AutoTraceItem;
import com.ywsuoyi.imgLoader.ImgLoader;
import com.ywsuoyi.imgLoader.TraceBlock;
import com.ywsuoyi.imgLoader.TraceCenterBlock;
import com.ywsuoyi.loadingThreadUtil.BaseThread;
import com.ywsuoyi.loadingThreadUtil.ThreadBlock;
import com.ywsuoyi.loadingThreadUtil.ThreadBlockEntity;
import com.ywsuoyi.mapLoader.MapLoader;
import com.ywsuoyi.projector.ProjectorBlock;
import com.ywsuoyi.projector.ProjectorBlockEntity;
import com.ywsuoyi.projector.ProjectorBlockItem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;


public class PixelLoader {
    public static final Logger logger = LogUtils.getLogger();
    public static final String MOD_ID = "pixel_loader";

    public static final Vec3i[] neb = new Vec3i[26];
    public static Item coloredBlockLoader;
    public static Item imgLoader;
    public static Item mapLoader;
    public static Item autoTracer;

    public static TraceBlock traceBlock;
    public static Item traceBlockItem;

    public static TraceCenterBlock traceCenterBlock;
    public static Item traceCenterBlockItem;

    public static ProjectorBlock projectorBlock;
    public static Item projectorBlockItem;
    public static BlockEntityType<ProjectorBlockEntity> projectorBlockEntity;
    public static ModelLayerLocation projectorBlockLayer = new ModelLayerLocation(PixelLoader.loc("projectorblock"), "projectorblock");
    public static Material projectorBlockMaterial = new Material(TextureAtlas.LOCATION_BLOCKS, loc("block/projectorblock"));

    public static Block threadBlock;
    public static BlockEntityType<ThreadBlockEntity> threadBlockEntity;
    public static ModelLayerLocation threadBlockLayer = new ModelLayerLocation(PixelLoader.loc("threadblock"), "threadblock");
    public static Material threadBlockMaterial = new Material(TextureAtlas.LOCATION_BLOCKS, loc("block/threadblock"));

    public static Block outlineBlock;

    public static CreativeModeTab TAB;

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ResourceLocation loc(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    @ExpectPlatform
    public static <T extends BlockEntity> BlockEntityType<T> buildBlockEntity(Factory<T> entity, Block block) {
        throw new AssertionError();
    }

    @FunctionalInterface
    public interface Factory<T extends BlockEntity> {
        T create(BlockPos blockPos, BlockState blockState);
    }

    public static void init() {
        int c = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                for (int k = -1; k < 2; k++) {
                    if (i != 0 || j != 0 || k != 0) {
                        neb[c++] = new Vec3i(i, j, k);
                    }
                }
            }
        }
        if (!Setting.imgFolder.exists())
            Setting.imgFolder.mkdir();
        Setting.banItem.set(0, new ItemStack(Items.TUBE_CORAL_BLOCK));
        Setting.banItem.set(1, new ItemStack(Items.BRAIN_CORAL_BLOCK));
        Setting.banItem.set(2, new ItemStack(Items.BUBBLE_CORAL_BLOCK));
        Setting.banItem.set(3, new ItemStack(Items.FIRE_CORAL_BLOCK));
        Setting.banItem.set(4, new ItemStack(Items.HORN_CORAL_BLOCK));
        Setting.banItem.set(5, new ItemStack(Items.BEDROCK));
        Setting.banItem.set(6, new ItemStack(Items.COPPER_BLOCK));
        Setting.banItem.set(7, new ItemStack(Items.CUT_COPPER));
        Setting.banItem.set(8, new ItemStack(Items.EXPOSED_COPPER));
        Setting.banItem.set(9, new ItemStack(Items.EXPOSED_CUT_COPPER));
        Setting.banItem.set(10, new ItemStack(Items.WEATHERED_COPPER));
        Setting.banItem.set(11, new ItemStack(Items.WEATHERED_CUT_COPPER));
        Setting.banItem.set(12, new ItemStack(Items.INFESTED_COBBLESTONE));
        Setting.banItem.set(13, new ItemStack(Items.INFESTED_CHISELED_STONE_BRICKS));
        Setting.banItem.set(14, new ItemStack(Items.INFESTED_CRACKED_STONE_BRICKS));
        Setting.banItem.set(15, new ItemStack(Items.INFESTED_DEEPSLATE));
        Setting.banItem.set(16, new ItemStack(Items.INFESTED_STONE));
        Setting.banItem.set(17, new ItemStack(Items.INFESTED_MOSSY_STONE_BRICKS));
        Setting.banItem.set(18, new ItemStack(Items.INFESTED_STONE_BRICKS));
        Setting.banItem.set(19, new ItemStack(Items.BUDDING_AMETHYST));
        Setting.banItem.set(20, new ItemStack(Items.ICE));
        ColorSpace.filter = Setting.banItem;
    }

    public static void end() {
        BaseThread.stopAllThread();
    }

    @ExpectPlatform
    public static CreativeModeTab regTab(String name, CreativeModeTab tab) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Item regItem(String name, Item item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Block> T regBlock(String name, T block) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends BlockEntity> BlockEntityType<T> regBlockEntity(String name, BlockEntityType<T> blockEntity) {
        throw new AssertionError();
    }

    public static void regAllTabs() {
        TAB = regTab("tab", CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                // Set name of tab to display
                .title(Component.translatable("item_group." + MOD_ID))
                // Set icon of creative tab
                .icon(() -> new ItemStack(PixelLoader.mapLoader))
                // Add default items to tab
                .displayItems((params, output) -> {
                    output.accept(PixelLoader.coloredBlockLoader);
                    output.accept(PixelLoader.imgLoader);
                    output.accept(PixelLoader.mapLoader);
                    output.accept(PixelLoader.autoTracer);

                    output.accept(PixelLoader.traceBlock);
                    output.accept(PixelLoader.traceCenterBlock);
                    output.accept(PixelLoader.projectorBlock);

                })
                .build());
    }

    public static void regAllBlocks() {
        outlineBlock = regBlock("outlineblock", new Block(BlockBehaviour.Properties.of().noOcclusion().noCollission()));
        threadBlock = regBlock("traceblock", new TraceBlock(BlockBehaviour.Properties.of().noOcclusion()));
        traceCenterBlock = regBlock("tracecenterblock", new TraceCenterBlock(BlockBehaviour.Properties.of().noOcclusion()));
        projectorBlock = regBlock("projectorblock", new ProjectorBlock(BlockBehaviour.Properties.of().noOcclusion().noCollission()));
        threadBlock = regBlock("threadblock", new ThreadBlock(BlockBehaviour.Properties.of().noOcclusion()));
    }

    public static void regAllItems() {
        coloredBlockLoader = regItem("colorspaceloader", new ColorSpaceLoader(new Item.Properties()));
        imgLoader = regItem("imgloader", new ImgLoader(new Item.Properties()));
        mapLoader = regItem("maploader", new MapLoader(new Item.Properties()));
        autoTracer = regItem("autotracer", new AutoTraceItem(new Item.Properties()));
        traceBlockItem = regItem("traceblock", new BlockItem(traceBlock, new Item.Properties()));
        traceCenterBlockItem = regItem("tracecenterblock", new BlockItem(traceCenterBlock, new Item.Properties()));
        projectorBlockItem = regItem("projectorblock", new ProjectorBlockItem(projectorBlock, new Item.Properties()));
    }

    public static void regAllBlockEntity() {
        projectorBlockEntity = regBlockEntity("projectorblockentity", buildBlockEntity(ProjectorBlockEntity::new, projectorBlock));
        threadBlockEntity = regBlockEntity("threadblockentity", buildBlockEntity(ThreadBlockEntity::new, threadBlock));
    }
}
