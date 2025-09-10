package com.ywsuoyi;

import com.mojang.logging.LogUtils;
import com.ywsuoyi.colorspace.ColorSpaces;
import com.ywsuoyi.colorspace.ColorSpaceLoader;
import com.ywsuoyi.loader.beaconLoader.BeaconLoader;
import com.ywsuoyi.simpleContent.AutoTraceItem;
import com.ywsuoyi.loader.imgLoader.ImgLoader;
import com.ywsuoyi.simpleContent.TraceBlock;
import com.ywsuoyi.simpleContent.TraceCenterBlock;
import com.ywsuoyi.loadingThreadUtil.BaseThread;
import com.ywsuoyi.loadingThreadUtil.ThreadBlock;
import com.ywsuoyi.loadingThreadUtil.ThreadBlockEntity;
import com.ywsuoyi.loader.mapLoader.MapLoader;
import com.ywsuoyi.projector.ProjectorBlock;
import com.ywsuoyi.projector.ProjectorBlockEntity;
import com.ywsuoyi.projector.ProjectorBlockItem;
import com.ywsuoyi.simpleContent.TagSplitItem;
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
    public static Item tagSplitter;
    public static Item coloredBlockLoader;
    public static Item imgLoader;
    public static Item mapLoader;
    public static Item beaconLoader;
    public static Item autoTracer;

    public static TraceBlock traceBlock;
    public static Item traceBlockItem;

    public static TraceCenterBlock traceCenterBlock;
    public static Item traceCenterBlockItem;

    public static ProjectorBlock projectorBlock;
    public static Item projectorBlockItem;
    public static BlockEntityType<ProjectorBlockEntity> projectorBlockEntity;
    public static ModelLayerLocation projectorBlockLayer = new ModelLayerLocation(PixelLoader.loc("projector"), "projector");
    public static Material projectorBlockMaterial = new Material(TextureAtlas.LOCATION_BLOCKS, loc("block/projector"));

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
        if (!ImageManager.imgFolder.exists())
            ImageManager.imgFolder.mkdir();
        ColorSpaces.filter.set(0, new ItemStack(Items.TUBE_CORAL_BLOCK));
        ColorSpaces.filter.set(1, new ItemStack(Items.BRAIN_CORAL_BLOCK));
        ColorSpaces.filter.set(2, new ItemStack(Items.BUBBLE_CORAL_BLOCK));
        ColorSpaces.filter.set(3, new ItemStack(Items.FIRE_CORAL_BLOCK));
        ColorSpaces.filter.set(4, new ItemStack(Items.HORN_CORAL_BLOCK));
        ColorSpaces.filter.set(5, new ItemStack(Items.BEDROCK));

        ColorSpaces.filter.set(6, new ItemStack(Items.COPPER_BLOCK));
        ColorSpaces.filter.set(7, new ItemStack(Items.CUT_COPPER));
        ColorSpaces.filter.set(8, new ItemStack(Items.COPPER_GRATE));
        ColorSpaces.filter.set(9, new ItemStack(Items.CHISELED_COPPER));
        ColorSpaces.filter.set(10, new ItemStack(Items.COPPER_BULB));

        ColorSpaces.filter.set(11, new ItemStack(Items.EXPOSED_COPPER));
        ColorSpaces.filter.set(12, new ItemStack(Items.EXPOSED_CUT_COPPER));
        ColorSpaces.filter.set(13, new ItemStack(Items.EXPOSED_COPPER_GRATE));
        ColorSpaces.filter.set(14, new ItemStack(Items.EXPOSED_CHISELED_COPPER));
        ColorSpaces.filter.set(15, new ItemStack(Items.EXPOSED_COPPER_BULB));

        ColorSpaces.filter.set(16, new ItemStack(Items.WEATHERED_COPPER));
        ColorSpaces.filter.set(17, new ItemStack(Items.WEATHERED_CUT_COPPER));
        ColorSpaces.filter.set(18, new ItemStack(Items.WEATHERED_COPPER_GRATE));
        ColorSpaces.filter.set(19, new ItemStack(Items.WEATHERED_CHISELED_COPPER));
        ColorSpaces.filter.set(20, new ItemStack(Items.WEATHERED_COPPER_BULB));

        ColorSpaces.filter.set(21, new ItemStack(Items.INFESTED_COBBLESTONE));
        ColorSpaces.filter.set(22, new ItemStack(Items.INFESTED_CHISELED_STONE_BRICKS));
        ColorSpaces.filter.set(23, new ItemStack(Items.INFESTED_CRACKED_STONE_BRICKS));
        ColorSpaces.filter.set(24, new ItemStack(Items.INFESTED_DEEPSLATE));
        ColorSpaces.filter.set(25, new ItemStack(Items.INFESTED_STONE));
        ColorSpaces.filter.set(26, new ItemStack(Items.INFESTED_MOSSY_STONE_BRICKS));
        ColorSpaces.filter.set(27, new ItemStack(Items.INFESTED_STONE_BRICKS));
        ColorSpaces.filter.set(28, new ItemStack(Items.BUDDING_AMETHYST));
        ColorSpaces.filter.set(29, new ItemStack(Items.ICE));
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
        TAB = regTab("pixelloader", CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .title(Component.translatable("item_group." + MOD_ID))
                .icon(() -> new ItemStack(PixelLoader.mapLoader))
                .displayItems((params, output) -> {
                    output.accept(PixelLoader.tagSplitter);
                    output.accept(PixelLoader.coloredBlockLoader);
                    output.accept(PixelLoader.imgLoader);
                    output.accept(PixelLoader.mapLoader);
                    output.accept(PixelLoader.beaconLoader);
                    output.accept(PixelLoader.autoTracer);

                    output.accept(PixelLoader.traceBlock);
                    output.accept(PixelLoader.traceCenterBlock);
                    output.accept(PixelLoader.projectorBlock);
                })
                .build());
    }

    public static void regAllBlocks() {
        outlineBlock = regBlock("outlineblock", new Block(BlockBehaviour.Properties.of().noOcclusion().noCollission()));
        traceBlock = regBlock("traceblock", new TraceBlock(BlockBehaviour.Properties.of().noOcclusion()));
        traceCenterBlock = regBlock("tracecenterblock", new TraceCenterBlock(BlockBehaviour.Properties.of().noOcclusion()));
        projectorBlock = regBlock("projectorblock", new ProjectorBlock(BlockBehaviour.Properties.of().noOcclusion().noCollission()));
        threadBlock = regBlock("threadblock", new ThreadBlock(BlockBehaviour.Properties.of().noOcclusion()));
    }

    public static void regAllItems() {
        tagSplitter = regItem("tagsplitter", new TagSplitItem(new Item.Properties()));
        coloredBlockLoader = regItem("colorspaceloader", new ColorSpaceLoader(new Item.Properties()));
        imgLoader = regItem("imgloader", new ImgLoader(new Item.Properties()));
        mapLoader = regItem("maploader", new MapLoader(new Item.Properties()));
        beaconLoader = regItem("beaconloader", new BeaconLoader(new Item.Properties()));
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
