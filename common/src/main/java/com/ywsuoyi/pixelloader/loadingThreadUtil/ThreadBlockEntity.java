package com.ywsuoyi.pixelloader.loadingThreadUtil;

import com.ywsuoyi.pixelloader.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;

public class ThreadBlockEntity extends BlockEntity {
    public int tick = 0;
    public HashMap<BlockPos, BlockState> blocks = new HashMap<>();


    public ThreadBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(PixelLoader.threadBlockEntity, blockPos, blockState);
    }

    public static void renderTick(Level level, BlockPos pos, BlockState state, ThreadBlockEntity entity) {
        entity.tick++;
        ThreadData data = ThreadData.data.get(pos);
        if (data != null && data.state == ThreadData.State.loading && entity.tick % 40 == 0) {
            entity.blocks.putAll(data.genBlocks);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ThreadBlockEntity entity) {
        ThreadData data = ThreadData.getData(pos);
        if (data != null && data.state == ThreadData.State.place) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            data.genBlocks.forEach((pos1, state1) -> {
                if (FallingBlock.isFree(level.getBlockState(pos1.below())))
                    level.setBlock(pos1.below(), Blocks.GLASS.defaultBlockState(), 3);
                level.setBlock(pos1, state1, 3);
            });
        }
    }
}
