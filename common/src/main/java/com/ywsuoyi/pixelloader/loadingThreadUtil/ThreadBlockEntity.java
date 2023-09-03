package com.ywsuoyi.pixelloader.loadingThreadUtil;

import com.ywsuoyi.pixelloader.PixelLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;

public class ThreadBlockEntity extends BlockEntity {
    public int tick = 0;


    public ThreadBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(PixelLoader.threadBlockEntity, blockPos, blockState);
    }

    public static void renderTick(Level level, BlockPos pos, BlockState state, ThreadBlockEntity entity) {
        entity.tick++;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ThreadBlockEntity entity) {
        ThreadData data = ThreadData.getData(pos);
        if (data != null && data.state == ThreadData.State.place) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            BlockPos center = data.center;
            data.genBlocks.forEach((tuple) -> {
                if (tuple.getB().getBlock() instanceof FallingBlock && FallingBlock.isFree(level.getBlockState(center.offset(tuple.getA().below()))))
                    level.setBlock(center.offset(tuple.getA().below()), Blocks.GLASS.defaultBlockState(), 3);
                level.setBlock(center.offset(tuple.getA()), tuple.getB(), 3);
            });
        }
    }
}
