package com.ywsuoyi.loader.beaconLoader;

import com.ywsuoyi.PixelLoader;
import com.ywsuoyi.colorspace.AbstractColorSpace;
import com.ywsuoyi.colorspace.ColorRGB;
import com.ywsuoyi.colorspace.ColorSpaces;
import com.ywsuoyi.colorspace.ColoredBlock;
import com.ywsuoyi.loadingThreadUtil.LoadingThread;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StainedGlassPaneBlock;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LoadBeaconThread extends LoadingThread {
    int genWidth;

    public LoadBeaconThread(Player player, File file, int dither, Level level, BlockPos center, int genWidth, int finish) {
        super(player, file, dither, 0, level, center, center);
        this.genWidth = genWidth;
        data.finish = finish;
    }

    @Override
    public void run() {
        if (file == null) {
            onend(false);
            return;
        }
        try {
            BufferedImage read = ImageIO.read(file);
            int width = read.getWidth(), height = read.getHeight();
            float scale = width / (float) genWidth;
            int genHeight = (int) (height / scale);

            for (int x = 0; x < genWidth; x++) {
                int curColor = 0;
                for (int y = 0; y < genHeight; y++) {
                    ColoredBlock block = getGlass(curColor, calcRGB(read.getRGB((int) (x * scale), (int) ((genHeight - 1 - y) * scale))).rgb);
                    if (block != AbstractColorSpace.air)
                        curColor = getColor(curColor, (StainedGlassPaneBlock) block.block);
                    ColorRGB c = new ColorRGB(curColor);
                    r -= c.r;
                    g -= c.g;
                    b -= c.b;
                    BlockPos pos = new BlockPos(x + 1, 2 + y, 2);
                    data.genBlocks.add(new Tuple<>(pos, block.block.defaultBlockState()));
                }
            }
            for (int i = 0; i < genWidth; i++) {
                BlockPos pos = new BlockPos(i + 1, 1, 2);
                data.genBlocks.add(new Tuple<>(pos, Blocks.BEACON.defaultBlockState()));
                data.genBlocks.add(new Tuple<>(pos.offset(0, -1, -1), Blocks.IRON_BLOCK.defaultBlockState()));
                data.genBlocks.add(new Tuple<>(pos.offset(0, -1, 1), Blocks.IRON_BLOCK.defaultBlockState()));
                data.genBlocks.add(new Tuple<>(pos.offset(0, -1, 0), Blocks.IRON_BLOCK.defaultBlockState()));
            }
            data.genBlocks.add(new Tuple<>(BlockPos.ZERO.offset(0, 0, 1), Blocks.IRON_BLOCK.defaultBlockState()));
            data.genBlocks.add(new Tuple<>(BlockPos.ZERO.offset(0, 0, 2), Blocks.IRON_BLOCK.defaultBlockState()));
            data.genBlocks.add(new Tuple<>(BlockPos.ZERO.offset(0, 0, 3), Blocks.IRON_BLOCK.defaultBlockState()));
            data.genBlocks.add(new Tuple<>(BlockPos.ZERO.offset(genWidth + 1, 0, 1), Blocks.IRON_BLOCK.defaultBlockState()));
            data.genBlocks.add(new Tuple<>(BlockPos.ZERO.offset(genWidth + 1, 0, 2), Blocks.IRON_BLOCK.defaultBlockState()));
            data.genBlocks.add(new Tuple<>(BlockPos.ZERO.offset(genWidth + 1, 0, 3), Blocks.IRON_BLOCK.defaultBlockState()));


        } catch (IOException e) {
            PixelLoader.logger.error("Failed to generate image: {}", e.getMessage());
            this.endMessage = (Component.translatable("pixelLoader.LoadingThread.error", e.getMessage()));
        }

        if (data.genBlocks.size() > 10000) data.renderPercentage = Math.round(1000000f / data.genBlocks.size()) / 100f;
        else data.renderPercentage = 1;

        processFinish("_map");
        onend(false);
    }

    public ColoredBlock getGlass(int curColor, int tarColor) {
        ColoredBlock result = AbstractColorSpace.air;
        ColorRGB curColorRGB = new ColorRGB(curColor);
        ColorRGB tarColorRGB = new ColorRGB(tarColor);
        float dist = ColorRGB.rgbSq(tarColorRGB, curColorRGB);
        for (ColoredBlock coloredBlock : ColorSpaces.beaconSpace) {
            ColorRGB newColor = new ColorRGB(getColor(curColor, (StainedGlassPaneBlock) coloredBlock.block));
            float newDist = ColorRGB.rgbSq(tarColorRGB, newColor);
            if (newDist < dist) {
                result = coloredBlock;
                dist = newDist;
            }
        }
        return result;
    }

    public int getColor(int curColor, StainedGlassPaneBlock block) {
        int textureDiffuseColor = block.getColor().getTextureDiffuseColor();
        if (curColor == 0) return textureDiffuseColor;
        else return FastColor.ARGB32.average(curColor, textureDiffuseColor);
    }
}
