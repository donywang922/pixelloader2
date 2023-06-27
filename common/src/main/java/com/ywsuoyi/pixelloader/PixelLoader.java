package com.ywsuoyi.pixelloader;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.function.Supplier;

public class PixelLoader {
    public static final String MOD_ID = "pixelloader";
    public static final Item coloredBlockLoader = new ColoredBlockLoader(new Item.Properties().tab(getTAB()));
    public static final Item imgLoader = new ImgLoader(new Item.Properties().tab(getTAB()));
    public static final Item mapLoader = new MapLoader(new Item.Properties().tab(getTAB()));
    public static final Item autoTracer = new AutoTraceItem(new Item.Properties().tab(getTAB()));
    public static final Block threadBlock = new ThreadBlock(BlockBehaviour.Properties.of(Material.STONE).noOcclusion());
    public static final TraceBlock traceBlock = new TraceBlock(BlockBehaviour.Properties.of(Material.STONE).noOcclusion());
    public static final Item traceBlockItem = new BlockItem(traceBlock, new Item.Properties().tab(getTAB()));

    public static final TraceCenterBlock traceCenterBlock = new TraceCenterBlock(BlockBehaviour.Properties.of(Material.STONE).noOcclusion());
    public static final Item traceCenterBlockItem = new BlockItem(traceCenterBlock, new Item.Properties().tab(getTAB()));

    public static final ProjectorBlock projectorBlock = new ProjectorBlock(BlockBehaviour.Properties.of(Material.STONE).noOcclusion());
    public static final Item projectorBlockItem = new BlockItem(projectorBlock, new Item.Properties().tab(getTAB()));
    public static final BlockEntityType<ProjectorBlockEntity> projectorBlockEntity = buildBlockEntity(ProjectorBlockEntity::new, projectorBlock);

    @ExpectPlatform
    public static CreativeModeTab getTAB() {
        throw new AssertionError();
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
                        TraceBlock.neb[c++] = i;
                        TraceBlock.neb[c++] = j;
                        TraceBlock.neb[c++] = k;
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
        Setting.banItem.set(19, new ItemStack(Items.ICE));
    }

    public static void end() {
        Setting.threads.forEach((integer, loadingThread) -> loadingThread.ForceStop());
    }
}
