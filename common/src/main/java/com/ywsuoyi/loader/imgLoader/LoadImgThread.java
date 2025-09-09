package com.ywsuoyi.loader.imgLoader;

import com.ywsuoyi.PixelLoader;
import com.ywsuoyi.colorspace.ColorSpaces;
import com.ywsuoyi.colorspace.ColoredBlock;
import com.ywsuoyi.loadingThreadUtil.ThreadData;
import com.ywsuoyi.simpleContent.TraceBlock;
import com.ywsuoyi.simpleContent.TraceCenterBlock;
import com.ywsuoyi.loadingThreadUtil.LoadingThread;
import com.ywsuoyi.loadingThreadUtil.BaseThread;
import com.ywsuoyi.loadingThreadUtil.SaveSchematicThread;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class LoadImgThread extends LoadingThread {
    public boolean trace = false;
    public List<BlockPos> axisX = new ArrayList<>();
    public List<BlockPos> axisY = new ArrayList<>();
    public CoordinateSystem coordinateSystem;
    public float scale;

    public LoadImgThread(Player player, File file, int dither, int cutout, int support, int cover, int finish,
                         CoordinateSystem coordinateSystem, float scale, Level level, BlockPos center, BlockPos anchor) {
        super(player, file, dither, cutout, level, center, anchor);
        data.support = support;
        data.cover = cover;
        data.finish = finish;
        if (level.getBlockState(center).is(PixelLoader.traceCenterBlock)) {
            axisY.add(BlockPos.ZERO);
            axisX.add(BlockPos.ZERO);
            BlockPos curpos = center.offset(TraceBlock.fromID(level.getBlockState(center).getValue(TraceCenterBlock.pointX)));
            axisX.add(curpos.subtract(center));
            int pid;
            BlockState st;
            while ((st = level.getBlockState(curpos)).is(PixelLoader.traceBlock) && (pid = st.getValue(TraceBlock.point)) != 0) {
                curpos = curpos.offset(TraceBlock.fromID(pid));
                if (axisX.contains(curpos.subtract(center))) break;
                else axisX.add(curpos.subtract(center));
            }

            curpos = center.offset(TraceBlock.fromID(level.getBlockState(center).getValue(TraceCenterBlock.pointY)));
            axisY.add(curpos.subtract(center));
            while ((st = level.getBlockState(curpos)).is(PixelLoader.traceBlock) && (pid = st.getValue(TraceBlock.point)) != 0) {
                curpos = curpos.offset(TraceBlock.fromID(pid));
                if (axisY.contains(curpos.subtract(center))) break;
                else axisY.add(curpos.subtract(center));
            }
            trace = true;
        }
        this.coordinateSystem = coordinateSystem;
        this.scale = scale;
    }

    @Override
    public void run() {
        try {
            BufferedImage read = ImageIO.read(file);
            int width = read.getWidth();
            int height = read.getHeight();
            if (trace) {
                if (width > height && axisX.size() < axisY.size() || width < height && axisX.size() > axisY.size()) {
                    List<BlockPos> tmp = axisX;
                    axisX = axisY;//rotate 90 degree
                    axisY = tmp;
                }
                int xScale = width / axisX.size();
                int yScale = height / axisY.size();
                axisY = axisY.reversed();
                scale = Math.min(xScale, yScale);
            }
            float uMax = width * scale, vMax = height * scale;
            for (int v = 0; v < vMax; v++) {
                for (int u = 0; u < uMax; u++) {
                    if (state == State.end) {
                        onend(true);
                        return;
                    }
                    ColoredBlock block = ColorSpaces.blockSpace.getBlock(calcRGB(read.getRGB((int) (u / scale), (int) (v / scale))));
                    r -= block.r;
                    g -= block.g;
                    b -= block.b;
                    if (trace) {
                        if (u < axisX.size() && v < axisY.size())
                            data.genBlocks.add(new Tuple<>(new BlockPos(axisX.get(u)).offset(axisY.get(v)), block.block.defaultBlockState()));
                    } else
                        data.genBlocks.add(new Tuple<>(coordinateSystem.getWorldCoords(u, v, (int) uMax, (int) vMax, 1), block.block.defaultBlockState()));
                }
                this.message = Component.literal(v + "/" + vMax);
            }
            postProcess();
        } catch (IOException e) {
            PixelLoader.logger.error("Failed to generate image: {}", e.getMessage());
            this.endMessage = Component.translatable("pixelLoader.LoadingThread.error", e.getMessage());
        }
        if (data.genBlocks.size() > 10000) data.renderPercentage = Math.round(1000000f / data.genBlocks.size()) / 100f;
        else data.renderPercentage = 1;

        // 处理完成参数
        processFinish();
        onend(false);
    }
}