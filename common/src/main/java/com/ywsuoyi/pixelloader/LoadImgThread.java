package com.ywsuoyi.pixelloader;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class LoadImgThread extends LoadingThread {

    public boolean pm;
    public BlockPos ct;

    public boolean trace = false;
    public NonNullList<BlockPos> axisX = NonNullList.create();
    public NonNullList<BlockPos> axisY = NonNullList.create();

    public LoadImgThread(File file, UseOnContext context, int cc, boolean pm, int no, boolean fs) {
        super(file, context, cc, no, fs);
        this.pm = pm;
        this.blockList = Setting.coloredBlocks;
        if (context.getLevel().getBlockState(context.getClickedPos()).is(PixelLoader.traceCenterBlock)) {
            axisY.add(BlockPos.ZERO);
            axisX.add(BlockPos.ZERO);
            ct = context.getClickedPos();

            BlockPos curpos = ct.offset(TraceBlock.fromID(world.getBlockState(ct).getValue(TraceCenterBlock.pointX)));
            axisX.add(curpos.subtract(ct));
            int pid;
            BlockState st;
            while ((st = world.getBlockState(curpos)).is(PixelLoader.traceBlock) && (pid = st.getValue(TraceBlock.point)) != 0) {
                curpos = curpos.offset(TraceBlock.fromID(pid));
                if (axisX.contains(curpos.subtract(ct))) break;
                else axisX.add(curpos.subtract(ct));
            }

            curpos = ct.offset(TraceBlock.fromID(world.getBlockState(ct).getValue(TraceCenterBlock.pointY)));
            axisY.add(curpos.subtract(ct));
            while ((st = world.getBlockState(curpos)).is(PixelLoader.traceBlock) && (pid = st.getValue(TraceBlock.point)) != 0) {
                curpos = curpos.offset(TraceBlock.fromID(pid));
                if (axisY.contains(curpos.subtract(ct))) break;
                else axisY.add(curpos.subtract(ct));
            }
            trace = true;
        } else ct = bp.east();
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
                    cc = xcc;
                    skipy = (height - axisY.size() * xcc) / 2;
                } else {
                    cc = ycc;
                    skipx = (width - axisX.size() * ycc) / 2;
                }
            }
            int yct = 0;
            for (int y = height - 1 - skipy; y >= read.getMinY() + skipy; y -= cc) {
                int xct = 0;
                for (int x = read.getMinX() + skipx; x < width - skipx; x += cc) {
                    if (!run) {
                        if (player != null)
                            player.displayClientMessage(Component.translatable("pixelLoader.LoadingThread.stop"), true);
                        Setting.threads.remove(no);
                        Setting.startNextThread();
                        return;
                    }
                    ColoredBlock block = CBlock(Setting.colorBlockMap, CRGB(read.getRGB(x, y)));
                    if (trace) {
                        if (xct < axisX.size() && yct < axisY.size())
                            world.setBlock(ct.offset(axisX.get(xct)).offset(axisY.get(yct)), block.block.defaultBlockState(), 3);
                    } else
                        world.setBlock(ct.offset(xct, pm ? 0 : yct, pm ? -yct : 0),
                                block.block.defaultBlockState(), 3);
                    xct++;
                }
                this.message = Component.literal((height - y) + "/" + height);
                yct++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        world.setBlock(bp, Blocks.AIR.defaultBlockState(), 3);
        Setting.threads.remove(no);
        Setting.startNextThread();
        if (player != null)
            player.displayClientMessage(Component.translatable("pixelLoader.LoadingThread.finish"), true);
    }
}
