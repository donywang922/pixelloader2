package com.ywsuoyi.pixelloader;

import com.ywsuoyi.pixelloader.colorspace.ColorRGB;
import com.ywsuoyi.pixelloader.colorspace.ColoredBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.awt.*;
import java.io.File;
import java.util.Map;

public class LoadingThread extends Thread {
    public Component message = Component.empty();
    public File file;
    public Player player;
    public BlockPos bp;
    public Level world;
    public boolean run = false;
    public boolean fs;
    public int cc;
    public int r = 0, g = 0, b = 0;
    public NonNullList<ColoredBlock> blockList;
    public int no;

    public LoadingThread(File file, int no, boolean fs, Level world, Player player) {
        this.file = file;
        this.no = no;
        this.fs = fs;
        this.world = world;
        this.player = player;
    }

    public LoadingThread(File file, UseOnContext context, int cc, int no, boolean fs) {
        this(file, no, fs, context.getLevel(), context.getPlayer());
        this.bp = context.getClickedPos().offset(context.getClickedFace().getNormal());
        context.getLevel().setBlock(bp, PixelLoader.threadBlock.defaultBlockState().setValue(ThreadBlock.threadNO, no), 3);
        this.cc = cc;
    }

    public ColorRGB CRGB(int rgb) {
        if (
                (Setting.cutout == 1 && ((rgb >> 16) & 0xFF) > 250 && ((rgb >> 8) & 0xFF) > 250 && (rgb & 0xFF) > 250) ||
                        (Setting.cutout == 2 && ((rgb >> 16) & 0xFF) < 5 && ((rgb >> 8) & 0xFF) < 5 && (rgb & 0xFF) < 5) ||
                        (Setting.cutout == 3 && ((rgb >> 24) & 0xFF) != 255)
        ) return null;
        if (fs) {
            r = Math.abs(r) > 64 ? 0 : r;
            g = Math.abs(g) > 64 ? 0 : g;
            b = Math.abs(b) > 64 ? 0 : b;
            r += (rgb >> 16) & 0xFF;
            g += (rgb >> 8) & 0xFF;
            b += rgb & 0xFF;
        } else {
            r = (rgb >> 16) & 0xFF;
            g = (rgb >> 8) & 0xFF;
            b = rgb & 0xFF;
        }
        return new ColorRGB(Mth.clamp(r, 0, 255), Mth.clamp(r, 0, 255), Mth.clamp(r, 0, 255));
    }

    public ColoredBlock CBlock(Map<Integer, ColoredBlock> blockMap, ColorRGB rgb) {
        if (rgb == null) {
            return Setting.air;
        }
        ColoredBlock block;
        if (blockMap.containsKey(rgb.rgb)) {
            return blockMap.get(rgb.rgb);
        }
        block = blockList.get(0);
        double d = Math.pow((block.r - rgb.r) * 0.30, 2) + Math.pow((block.g - rgb.g) * 0.59, 2) + Math.pow((block.b - rgb.b) * 0.11, 2);
        for (ColoredBlock coloredBlock : blockList) {
            if (Setting.lt || coloredBlock.y == 0) {
                double t = Math.pow((coloredBlock.r - rgb.r) * 0.30, 2) + Math.pow((coloredBlock.g - rgb.g) * 0.59, 2) + Math.pow((coloredBlock.b - rgb.b) * 0.11, 2);
                if (t < d) {
                    d = t;
                    block = coloredBlock;
                }
            }
        }
        blockMap.put(rgb.rgb, block);

        return block;
    }


    @Override
    public synchronized void start() {
        run = true;
        if (player != null)
            player.displayClientMessage(Component.translatable("pixelLoader.LoadingThread.start"), true);
        super.start();
    }

    public void ForceStop() {
        this.run = false;
    }
}
