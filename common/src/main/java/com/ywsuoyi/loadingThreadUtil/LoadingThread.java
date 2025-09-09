package com.ywsuoyi.loadingThreadUtil;

import com.ywsuoyi.PixelLoader;
import com.ywsuoyi.colorspace.ColorRGB;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoadingThread extends BaseThread {
    public boolean dither;
    public File file;
    public int cutout;
    public BlockPos anchor;
    public Level level;
    public ThreadData data;

    public int r = 0, g = 0, b = 0;

    public LoadingThread(Player player, File file, int dither, int cutout, Level level, BlockPos center, BlockPos anchor) {
        super(player);
        this.file = file;
        this.dither = dither == 1;
        this.cutout = cutout;
        this.anchor = anchor;
        this.level = level;
        this.data = ThreadData.getData(this);
        data.center = center;
        if (level.getBlockState(anchor).getBlock() != PixelLoader.projectorBlock)
            level.setBlock(anchor, PixelLoader.threadBlock.defaultBlockState(), 3);
    }

    public ColorRGB calcRGB(int rgb) {
        int tr = ((rgb >> 16) & 0xFF), tg = ((rgb >> 8) & 0xFF), tb = (rgb & 0xFF);
        if ((cutout == 1 && tr > 250 && tg > 250 && tb > 250) ||
                (cutout == 2 && tr < 5 && tg < 5 && tb < 5) ||
                (cutout == 3 && ((rgb >> 24) & 0xFF) != 255)
        ) return null;
        if (dither) {
            r = Math.abs(r) > 64 ? tr : r + tr;
            g = Math.abs(g) > 64 ? tg : g + tg;
            b = Math.abs(b) > 64 ? tb : b + tb;
        } else {
            r = tr;
            g = tg;
            b = tb;
        }
        return new ColorRGB(Mth.clamp(r, 0, 255), Mth.clamp(g, 0, 255), Mth.clamp(b, 0, 255));
    }

    public void postProcess() {

        // 创建方块位置集合用于快速查找
        Set<BlockPos> generatedBlocks = new HashSet<>();
        for (Tuple<BlockPos, BlockState> tuple : data.genBlocks) {
            if (tuple.getB().isAir()) continue;
            generatedBlocks.add(tuple.getA());
        }
        // 处理覆盖方块
        int total = data.genBlocks.size();
        processCoverBlocks(generatedBlocks, total);
        // 处理支撑方块
        total = data.genBlocks.size();
        processSupportBlocks(generatedBlocks, total);
    }

    public void processSupportBlocks(Set<BlockPos> generatedBlocks, int total) {
        List<Tuple<BlockPos, BlockState>> supportBlocks = new ArrayList<>();

        int processCount = 0;
        for (Tuple<BlockPos, BlockState> tuple : data.genBlocks) {
            if (state == State.end) {
                onend(true);
                return;
            }
            if (tuple.getB().isAir()) continue;

            BlockPos pos = tuple.getA();

            if (data.support == 1) {
                // 在所有方块下方放玻璃
                BlockPos belowPos = pos.below();
                if (!generatedBlocks.contains(belowPos) && !level.getBlockState(data.center.offset(belowPos)).isSolid()) {
                    supportBlocks.add(new Tuple<>(belowPos, Blocks.GLASS.defaultBlockState()));
                    generatedBlocks.add(belowPos);
                }
            } else if (data.support == 0) {
                // 只在可以下落的方块下方放玻璃
                if (canBlockFall(tuple.getB())) {
                    BlockPos belowPos = pos.below();
                    if (!generatedBlocks.contains(belowPos) && !level.getBlockState(data.center.offset(belowPos)).isSolid()) {
                        supportBlocks.add(new Tuple<>(belowPos, Blocks.GLASS.defaultBlockState()));
                        generatedBlocks.add(belowPos);
                    }
                }
            } else if (data.support == 2) {
                // 用玻璃充填直到碰到另一个方块
                BlockPos currentPos = pos.below();
                while (!generatedBlocks.contains(currentPos) && !level.getBlockState(data.center.offset(currentPos)).isSolid()) {
                    supportBlocks.add(new Tuple<>(currentPos, Blocks.GLASS.defaultBlockState()));
                    generatedBlocks.add(currentPos);
                    currentPos = currentPos.below();

                    // 防止无限循环，设置一个合理的深度限制
                    if (currentPos.getY() < level.getMinBuildHeight()) break;
                }
            }
            this.message = Component.translatable("pixelLoader.LoadingThread.post", processCount++ + "/" + total);
        }
        data.genBlocks.addAll(supportBlocks);
    }

    public void processCoverBlocks(Set<BlockPos> generatedBlocks, int total) {
        if (data.cover == 0) return;
        List<Tuple<BlockPos, BlockState>> coverBlocks = new ArrayList<>();
        if (data.cover == 1) {
            // 在所有方块上方放玻璃
            int processCount = 0;
            for (Tuple<BlockPos, BlockState> tuple : new ArrayList<>(data.genBlocks)) {
                if (state == State.end) {
                    onend(true);
                    return;
                }
                if (tuple.getB().isAir()) continue;

                BlockPos pos = tuple.getA();
                BlockPos abovePos = pos.above();
                if (!generatedBlocks.contains(abovePos) && level.getBlockState(data.center.offset(abovePos)).isAir()) {
                    coverBlocks.add(new Tuple<>(abovePos, Blocks.GLASS.defaultBlockState()));
                    generatedBlocks.add(abovePos);
                }
                this.message = Component.translatable("pixelLoader.LoadingThread.post", processCount++ + "/" + total);
            }
            data.genBlocks.addAll(coverBlocks);
        }
    }

    public boolean canBlockFall(BlockState blockState) {
        // 检查方块是否可以下落（如沙子、砾石等）
        return blockState.getBlock() instanceof FallingBlock;
    }

    public void processFinish(String surfix) {
        // 如果finish是2或3，创建保存NBT线程
        if (data.finish == 2 || data.finish == 3) {
            String fileName = file.getName();
            if (fileName.contains(".")) {
                fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            }
            SaveSchematicThread saveThread = new SaveSchematicThread(player, data.genBlocks, fileName);
            BaseThread.addThread(saveThread);
        }

        // 如果finish是1或3，设置状态为place
        if (data.finish == 1 || data.finish == 3) {
            data.state = ThreadData.State.place;
        }
    }
}
