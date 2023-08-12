package com.ywsuoyi.pixelloader.loadingThreadUtil;

import com.ywsuoyi.pixelloader.PixelLoader;
import com.ywsuoyi.pixelloader.colorspace.ColorRGB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.io.File;

public class LoadingThread extends BaseThread {
    public boolean dither;
    public int size;
    public File file;
    public int cutout;
    public BlockPos center;
    public BlockPos anchor;
    public Level level;
    public ThreadData data;

    public int r = 0, g = 0, b = 0;

    public LoadingThread(Player player, File file, boolean dither, int size, int cutout, Level level, BlockPos center, BlockPos anchor) {
        super(player);
        this.file = file;
        this.dither = dither;
        this.size = size;
        this.cutout = cutout;
        this.center = center;
        this.anchor = anchor;
        this.level = level;
        this.data = ThreadData.getData(this);
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
}
