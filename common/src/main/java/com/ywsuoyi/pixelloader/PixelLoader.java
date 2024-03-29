package com.ywsuoyi.pixelloader;

import com.mojang.logging.LogUtils;
import com.ywsuoyi.pixelloader.colorspace.ColorSpace;
import com.ywsuoyi.pixelloader.colorspace.ColorSpaceLoader;
import com.ywsuoyi.pixelloader.imgLoader.AutoTraceItem;
import com.ywsuoyi.pixelloader.imgLoader.ImgLoader;
import com.ywsuoyi.pixelloader.imgLoader.TraceBlock;
import com.ywsuoyi.pixelloader.imgLoader.TraceCenterBlock;
import com.ywsuoyi.pixelloader.loadingThreadUtil.BaseThread;
import com.ywsuoyi.pixelloader.loadingThreadUtil.ThreadBlock;
import com.ywsuoyi.pixelloader.loadingThreadUtil.ThreadBlockEntity;
import com.ywsuoyi.pixelloader.mapLoader.MapLoader;
import com.ywsuoyi.pixelloader.projector.ProjectorBlock;
import com.ywsuoyi.pixelloader.projector.ProjectorBlockEntity;
import com.ywsuoyi.pixelloader.projector.ProjectorBlockItem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.slf4j.Logger;

public class PixelLoader {
    public static final Logger logger = LogUtils.getLogger();
    public static final String MOD_ID = "pixelloader";

    public static final Vec3i[] neb = new Vec3i[26];
    public static final Item coloredBlockLoader = new ColorSpaceLoader(new Item.Properties().tab(getTAB()));
    public static final Item imgLoader = new ImgLoader(new Item.Properties().tab(getTAB()));
    public static final Item mapLoader = new MapLoader(new Item.Properties().tab(getTAB()));
    public static final Item autoTracer = new AutoTraceItem(new Item.Properties().tab(getTAB()));

    public static final TraceBlock traceBlock = new TraceBlock(BlockBehaviour.Properties.of(Material.STONE).noOcclusion());
    public static final Item traceBlockItem = new BlockItem(traceBlock, new Item.Properties().tab(getTAB()));

    public static final TraceCenterBlock traceCenterBlock = new TraceCenterBlock(BlockBehaviour.Properties.of(Material.STONE).noOcclusion());
    public static final Item traceCenterBlockItem = new BlockItem(traceCenterBlock, new Item.Properties().tab(getTAB()));

    public static final ProjectorBlock projectorBlock = new ProjectorBlock(BlockBehaviour.Properties.of(Material.STONE).noOcclusion().noCollission());
    public static final Item projectorBlockItem = new ProjectorBlockItem(projectorBlock, new Item.Properties().tab(getTAB()));
    public static final BlockEntityType<ProjectorBlockEntity> projectorBlockEntity = buildBlockEntity(ProjectorBlockEntity::new, projectorBlock);

    public static final Block threadBlock = new ThreadBlock(BlockBehaviour.Properties.of(Material.STONE).noOcclusion());
    public static final Item threadBlockItem = new ProjectorBlockItem(threadBlock, new Item.Properties().tab(getTAB()));
    public static final BlockEntityType<ThreadBlockEntity> threadBlockEntity = buildBlockEntity(getThreadBlock(), threadBlock);

    public static final Block outlineBlock = new Block(BlockBehaviour.Properties.of(Material.STONE).noOcclusion().noCollission());

    @ExpectPlatform
    public static CreativeModeTab getTAB() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends BlockEntity> BlockEntityType<T> buildBlockEntity(Factory<T> entity, Block block) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Factory<ThreadBlockEntity> getThreadBlock() {
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
}
