package com.ywsuoyi.loader.imgLoader;

import com.ywsuoyi.ImageManager;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

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
            if (trace) {
                // Trace模式：如果是文件夹，读取第一张图片
                processTraceMode();
            } else {
                // 普通模式：如果是文件夹，处理所有图片；如果是文件，处理单个图片
                processNormalMode();
            }

            postProcess();
        } catch (IOException e) {
            PixelLoader.logger.error("Failed to generate image: {}", e.getMessage());
            this.endMessage = Component.translatable("pixelLoader.LoadingThread.error", e.getMessage());
        }

        if (data.genBlocks.size() > 10000) data.renderPercentage = Math.round(1000000f / data.genBlocks.size()) / 100f;
        else data.renderPercentage = 1;

        // 处理完成参数
        processFinish("_pixel");
        onend(false);
    }

    private void processTraceMode() throws IOException {
        File imageFile;

        if (file.isDirectory()) {
            // 文件夹模式：获取第一张图片
            imageFile = ImageManager.getFirstImageFromDirectory(file);
            if (imageFile == null) {
                this.endMessage = Component.translatable("pixelLoader.fileNotFind");
                return;
            }
        } else {
            // 文件模式
            imageFile = file;
        }

        BufferedImage read = ImageIO.read(imageFile);
        processSingleImageTrace(read, imageFile.getName());
    }

    private void processNormalMode() throws IOException {
        if (file.isDirectory()) {
            // 文件夹模式：处理所有图片
            List<File> imageFiles = ImageManager.getAllImagesFromDirectory(file);
            if (imageFiles.isEmpty()) {
                this.endMessage = Component.translatable("pixelLoader.fileNotFind");
                return;
            }

            int imageIndex = 0;
            for (File imageFile : imageFiles) {
                if (state == State.end) {
                    onend(true);
                    return;
                }

                try {
                    BufferedImage read = ImageIO.read(imageFile);
                    processSingleImageNormal(read, imageIndex, imageFile.getName());
                    imageIndex++;
                } catch (IOException e) {
                    PixelLoader.logger.warn("Failed to read image {}: {}", imageFile.getName(), e.getMessage());
                    continue;
                }
            }
        } else {
            // 文件模式：处理单个图片
            BufferedImage read = ImageIO.read(file);
            processSingleImageNormal(read, 0, file.getName());
        }
    }

    private void processSingleImageTrace(BufferedImage read, String imageName) {
        int width = read.getWidth();
        int height = read.getHeight();

        if (width > height && axisX.size() < axisY.size() || width < height && axisX.size() > axisY.size()) {
            List<BlockPos> tmp = axisX;
            axisX = axisY; // rotate 90 degree
            axisY = tmp;
        }

        float xScale = axisX.size() / (float) width;
        float yScale = axisY.size() / (float) height;
        axisY = axisY.reversed();
        scale = Math.max(xScale, yScale);

        float uMax = width * scale;
        float vMax = height * scale;

        for (int v = 0; v < vMax; v++) {
            for (int u = 0; u < uMax; u++) {
                if (state == State.end) return;

                ColoredBlock block = ColorSpaces.blockSpace.getBlock(calcRGB(read.getRGB((int) (u / scale), (int) (v / scale))));
                r -= block.r;
                g -= block.g;
                b -= block.b;

                if (u < axisX.size() && v < axisY.size()) {
                    data.genBlocks.add(new Tuple<>(new BlockPos(axisX.get(u)).offset(axisY.get(v)), block.block.defaultBlockState()));
                }
            }
            this.message = Component.translatable("pixelLoader.LoadingThread.gen", imageName + " " + (v + 1) + "/" + (int)vMax);
        }
    }

    private void processSingleImageNormal(BufferedImage read, int imageIndex, String imageName) {
        int width = read.getWidth();
        int height = read.getHeight();
        float uMax = width * scale;
        float vMax = height * scale;

        for (int v = 0; v < vMax; v++) {
            for (int u = 0; u < uMax; u++) {
                if (state == State.end) return;

                ColoredBlock block = ColorSpaces.blockSpace.getBlock(calcRGB(read.getRGB((int) (u / scale), (int) (v / scale))));
                r -= block.r;
                g -= block.g;
                b -= block.b;

                // 使用图片编号作为第五个参数
                BlockPos worldPos = coordinateSystem.getWorldCoords(u, v, (int) uMax, (int) vMax, imageIndex);
                data.genBlocks.add(new Tuple<>(worldPos, block.block.defaultBlockState()));
            }
            this.message = Component.translatable("pixelLoader.LoadingThread.gen", imageName + " " + (v + 1) + "/" + (int)vMax);
        }
    }
}