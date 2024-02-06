package com.ywsuoyi.pixelloader.mapLoader;

import com.ywsuoyi.pixelloader.PixelLoader;
import com.ywsuoyi.pixelloader.Setting;
import com.ywsuoyi.pixelloader.colorspace.AbstractColorSpace;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.MaterialColor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LoadMapThread extends LoadingThread {
    Setting.MapMode mode;

    public LoadMapThread(Player player, File file, boolean dither, int size, int cutout, Level level, BlockPos anchor, Setting.MapMode mode) {
        super(player, file, dither, size, cutout, level, new BlockPos(Mth.floor((anchor.getX() + 64.0D) / 128d) * 128 - 64, anchor.getY(), Mth.floor((anchor.getZ() + 64.0D) / 128d) * 128 - 64), anchor);
        this.mode = mode;
    }

    @Override
    public void run() {
        try {
            BufferedImage read = ImageIO.read(file);
            int width = read.getWidth(), height = read.getHeight();
            float border = Math.max(width, height);
            int mapW = Math.round(width / border * 128 * size), mapH = Math.round(height / border * 128 * size);
            if (mode == Setting.MapMode.cover || mode == Setting.MapMode.cover_c || mode == Setting.MapMode.cover_c2) {
                int cx = data.center.getX(), cy = data.center.getZ();
                for (int x = 0; x < mapW; x++) {
                    double d = 0.0;
                    for (int y = 0; y < mapH; y++) {
                        if (state == State.end) {
                            onend(true);
                            return;
                        }
                        int aa = level.getHeight(Heightmap.Types.WORLD_SURFACE, cx + x, cy + y);
                        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
                        BlockState blockState;
                        if (aa > level.getMinBuildHeight() + 1) {
                            while (true) {
                                mutableBlockPos.set(cx + x, --aa, cy + y);
                                blockState = level.getBlockState(mutableBlockPos);
                                if (aa > level.getMinBuildHeight()) {
                                    if (blockState.is(PixelLoader.threadBlock)) continue;
                                    if (blockState.getDestroySpeed(level, mutableBlockPos) == 0 && blockState.getFluidState().isEmpty())
                                        continue;
                                    if (blockState.getMapColor(level, mutableBlockPos) == MaterialColor.NONE) continue;
                                }
                                break;
                            }
                        }
                        double f = aa - d + ((double) (x + y & 1) - 0.5) * 0.4;
                        int px = Math.round((float) width / mapW * x), py = Math.round((float) height / mapH * y);
                        ColoredBlock block;
                        int rgb = read.getRGB(px, py);
                        if (mode != Setting.MapMode.cover) {
                            int i = mode == Setting.MapMode.cover_c ? 220 : 180;
                            int j = (rgb >> 16 & 0xFF) * i / 255;
                            int k = (rgb >> 8 & 0xFF) * i / 255;
                            int l = (rgb & 0xFF) * i / 255;
                            rgb = 0xFF000000 | j << 16 | k << 8 | l;
                        }
                        if (f > 0.6) {
                            block = ColorSpace.mapUpSpace.getBlock(calcRGB(rgb));
                        } else if (f < -0.6) {
                            block = ColorSpace.mapDownSpace.getBlock(calcRGB(rgb));
                        } else {
                            block = ColorSpace.map0Space.getBlock(calcRGB(rgb));
                        }
                        d = aa;
                        r -= block.r;
                        g -= block.g;
                        b -= block.b;
                        data.genBlocks.add(new Tuple<>(mutableBlockPos.move(-cx, -data.center.getY(), -cy), block.block.defaultBlockState()));
                    }
                    this.message = Component.literal(x + "/" + mapW);
                }
            } else {
                ArrayList<Tuple<Integer, Block>> line = new ArrayList<>();
                int tmpY, minY, i;
                AbstractColorSpace space = mode == Setting.MapMode.flat ? ColorSpace.map0Space : ColorSpace.mapSpace;
                for (int x = 0; x < mapW; x++) {
                    tmpY = minY = i = 0;
                    line.clear();
                    for (int y = 0; y < mapH; y++) {
                        if (state == State.end) {
                            onend(true);
                            return;
                        }
                        int px = Math.round((float) width / mapW * x), py = Math.round((float) height / mapH * y);
                        ColoredBlock block = space.getBlock(calcRGB(read.getRGB(px, py)));
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
            }
        } catch (IOException e) {
            PixelLoader.logger.error("Failed to generate image: {}", e.getMessage());
        }
        if (data.genBlocks.size() > 10000) data.renderPercentage = Math.round(1000000f / data.genBlocks.size()) / 100f;
        else data.renderPercentage = 1;
        onend(false);
    }
}
