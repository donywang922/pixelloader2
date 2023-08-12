package com.ywsuoyi.pixelloader.imgLoader;


import com.ywsuoyi.pixelloader.PixelLoader;
import com.ywsuoyi.pixelloader.TraceBlock;
import com.ywsuoyi.pixelloader.TraceCenterBlock;
import com.ywsuoyi.pixelloader.colorspace.ColorSpace;
import com.ywsuoyi.pixelloader.colorspace.ColoredBlock;
import com.ywsuoyi.pixelloader.loadingThreadUtil.LoadingThread;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LoadImgThread extends LoadingThread {
    public boolean flat;
    public boolean trace = false;
    public NonNullList<BlockPos> axisX = NonNullList.create();
    public NonNullList<BlockPos> axisY = NonNullList.create();

    public LoadImgThread(Player player, File file, boolean dither, int size, int cutout, Level level, BlockPos center, BlockPos anchor, boolean flat) {
        super(player, file, dither, size, cutout, level, center, anchor);
        this.flat = flat;
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
    }

    @Override
    public void run() {
        try {
            BufferedImage read = ImageIO.read(file);
            int width = read.getWidth();
            int height = read.getHeight();
            int skipx = 0;
            int skipy = 0;
            if (trace) {
                if (width > height && axisX.size() < axisY.size() || width < height && axisX.size() > axisY.size()) {
                    NonNullList<BlockPos> tmp = axisX;
                    axisX = axisY;
                    axisY = tmp;
                }
                int xcc = width / axisX.size();
                int ycc = height / axisY.size();
                if (xcc < ycc) {
                    size = xcc;
                    skipy = (height - axisY.size() * xcc) / 2;
                } else {
                    size = ycc;
                    skipx = (width - axisX.size() * ycc) / 2;
                }
            }
            int yct = 0;
            for (int y = height - 1 - skipy; y >= read.getMinY() + skipy; y -= size) {
                int xct = 0;
                for (int x = read.getMinX() + skipx; x < width - skipx; x += size) {
                    if (state == State.end) {
                        onend(true);
                        return;
                    }
                    ColoredBlock block = ColorSpace.blockSpace.getBlockTree(calcRGB(read.getRGB(x, y)));
                    r -= block.r;
                    g -= block.g;
                    b -= block.b;
                    if (trace) {
                        if (xct < axisX.size() && yct < axisY.size())
                            data.genBlocks.put(center.offset(axisX.get(xct)).offset(axisY.get(yct)), block.block.defaultBlockState());
                    } else
                        data.genBlocks.put(center.offset(xct, flat ? 0 : yct, flat ? -yct : 0), block.block.defaultBlockState());
                    xct++;
                }
                this.message = Component.literal((height - y) + "/" + height);
                yct++;
            }
        } catch (IOException e) {
            PixelLoader.logger.error("Failed to generate image: {}", e.getMessage());
        }
        onend(false);
    }
}
