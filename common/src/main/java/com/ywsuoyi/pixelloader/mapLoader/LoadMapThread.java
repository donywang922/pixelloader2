package com.ywsuoyi.pixelloader.mapLoader;

import com.ywsuoyi.pixelloader.PixelLoader;
import com.ywsuoyi.pixelloader.colorspace.ColorSpace;
import com.ywsuoyi.pixelloader.colorspace.ColoredBlock;
import com.ywsuoyi.pixelloader.loadingThreadUtil.LoadingThread;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LoadMapThread extends LoadingThread {
    boolean flat;

    public LoadMapThread(Player player, File file, boolean dither, int size, int cutout, Level level, BlockPos anchor, boolean flat) {
        super(player, file, dither, size, cutout, level,
                new BlockPos(Mth.floor((anchor.getX() + 64.0D) / 128d) * 128 - 64,
                        anchor.getY(),
                        Mth.floor((anchor.getZ() + 64.0D) / 128d) * 128 - 64),
                anchor);
        this.flat = flat;
    }

    @Override
    public void run() {
        try {
            BufferedImage read = ImageIO.read(file);
            int width = read.getWidth(), height = read.getHeight();
            float border = Math.max(width, height);
            int mapW = Math.round(width / border * 128 * size), mapH = Math.round(height / border * 128 * size);
            ArrayList<Tuple<Integer, Block>> line = new ArrayList<>();
            int tmpY, minY, i;
            ColorSpace space = flat ? ColorSpace.mapFlatSpace : ColorSpace.mapSpace;
            for (int x = 0; x < mapW; x++) {
                tmpY = minY = i = 0;
                line.clear();
                for (int y = 0; y < mapH; y++) {
                    if (state == State.end) {
                        onend(true);
                        return;
                    }
                    int px = Math.round((float) width / mapW * x), py = Math.round((float) height / mapH * y);
                    ColoredBlock block = space.getBlockTree(calcRGB(read.getRGB(px, py)));
                    r -= block.r;
                    g -= block.g;
                    b -= block.b;
                    tmpY += block.y;
                    if (tmpY < minY) minY = tmpY;
                    line.add(new Tuple<>(tmpY, block.block));
                }
                for (Tuple<Integer, Block> tuple : line) {
                    BlockPos pos = new BlockPos(x, tuple.getA() - minY, i);
                    data.genBlocks.add(new Tuple<>(pos, tuple.getB().defaultBlockState()));
                    i++;
                }
                this.message = Component.literal(x + "/" + mapW);
            }

        } catch (IOException e) {
            PixelLoader.logger.error("Failed to generate image: {}", e.getMessage());
        }
        if (data.genBlocks.size() > 10000)
            data.renderPercentage = Math.round(1000000f / data.genBlocks.size()) / 100f;
        else
            data.renderPercentage = 1;
        onend(false);
    }
}
