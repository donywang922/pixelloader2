package com.ywsuoyi.loader.mapLoader;

import com.ywsuoyi.PixelLoader;
import com.ywsuoyi.colorspace.AbstractColorSpace;
import com.ywsuoyi.colorspace.ColorSpaces;
import com.ywsuoyi.colorspace.ColoredBlock;
import com.ywsuoyi.loadingThreadUtil.LoadingThread;
import com.ywsuoyi.loadingThreadUtil.BaseThread;
import com.ywsuoyi.loadingThreadUtil.SaveSchematicThread;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.MapColor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class LoadMapThread extends LoadingThread {
    MapSetting.MapMode mode;
    int size;
    int fit;

    public LoadMapThread(Player player, File file, int dither, int cutout, int support, int cover, int finish,
                         Level level, BlockPos anchor, int size, MapSetting.MapMode mode,int fit) {
        super(player, file, dither, cutout, level, new BlockPos(Mth.floor((anchor.getX() + 64.0D) / 128d) * 128 - 64, anchor.getY(), Mth.floor((anchor.getZ() + 64.0D) / 128d) * 128 - 64), anchor);
        data.support = support;
        data.cover = cover;
        data.finish = finish;
        this.mode = mode;
        this.size = size;
        this.fit = fit;
    }

    @Override
    public void run() {
        try {
            BufferedImage read = ImageIO.read(file);
            int width = read.getWidth(), height = read.getHeight();
            float border = Math.max(width, height);
            int mapW = Math.round(width / border * 128 * size), mapH = Math.round(height / border * 128 * size);
            if (mode == MapSetting.MapMode.cover || mode == MapSetting.MapMode.cover_c || mode == MapSetting.MapMode.cover_c2) {
                generateCoverMode(read, width, height, mapW, mapH);
            } else {
                generateStaircaseMode(read, width, height, mapW, mapH);
            }

            postProcess();
        } catch (IOException e) {
            PixelLoader.logger.error("Failed to generate image: {}", e.getMessage());
            this.endMessage = (Component.translatable("pixelLoader.LoadingThread.error", e.getMessage()));
        }

        if (data.genBlocks.size() > 10000) data.renderPercentage = Math.round(1000000f / data.genBlocks.size()) / 100f;
        else data.renderPercentage = 1;

        processFinish();
        onend(false);
    }

    private void generateCoverMode(BufferedImage read, int width, int height, int mapW, int mapH) {
        int cx = data.center.getX(), cy = data.center.getZ();
        for (int x = 0; x < mapW; x++) {
            if (state == State.end) return;

            double d = 0.0;
            for (int y = 0; y < mapH; y++) {
                if (state == State.end) return;

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
                            if (blockState.getMapColor(level, mutableBlockPos) == MapColor.NONE) continue;
                        }
                        break;
                    }
                }
                double f = aa - d + ((double) (x + y & 1) - 0.5) * 0.4;
                int px = Math.round((float) width / mapW * x), py = Math.round((float) height / mapH * y);
                ColoredBlock block;
                int rgb = read.getRGB(px, py);
                if (mode != MapSetting.MapMode.cover) {
                    int i = mode == MapSetting.MapMode.cover_c ? 220 : 180;
                    int j = (rgb >> 16 & 0xFF) * i / 255;
                    int k = (rgb >> 8 & 0xFF) * i / 255;
                    int l = (rgb & 0xFF) * i / 255;
                    rgb = 0xFF000000 | j << 16 | k << 8 | l;
                }
                if (f > 0.6) {
                    block = ColorSpaces.mapUpSpace.getBlock(calcRGB(rgb));
                } else if (f < -0.6) {
                    block = ColorSpaces.mapDownSpace.getBlock(calcRGB(rgb));
                } else {
                    block = ColorSpaces.map0Space.getBlock(calcRGB(rgb));
                }
                d = aa;
                r -= block.r;
                g -= block.g;
                b -= block.b;
                data.genBlocks.add(new Tuple<>(mutableBlockPos.move(-cx, -data.center.getY(), -cy), block.block.defaultBlockState()));
            }
            this.message = Component.literal(x + "/" + mapW);
        }
    }

    private void generateStaircaseMode(BufferedImage read, int width, int height, int mapW, int mapH) {
        AbstractColorSpace space = mode == MapSetting.MapMode.flat ? ColorSpaces.map0Space : ColorSpaces.mapSpace;

        for (int x = 0; x < mapW; x++) {
            if (state == State.end) return;

            // 生成原始高度数据和方块数据
            List<ColoredBlock> blocks = new ArrayList<>();
            List<Integer> rawHeights = new ArrayList<>();

            for (int y = 0; y < mapH; y++) {
                int px = Math.round((float) width / mapW * x), py = Math.round((float) height / mapH * y);
                ColoredBlock block = space.getBlock(calcRGB(read.getRGB(px, py)));
                r -= block.r;
                g -= block.g;
                b -= block.b;

                blocks.add(block);
                rawHeights.add(block.y);
            }

            // 使用阶梯截断算法优化高度
            List<Integer> optimizedHeights = optimizeStaircaseHeights(rawHeights);

            // 生成方块
            for (int i = 0; i < blocks.size(); i++) {
                BlockPos pos = new BlockPos(x, optimizedHeights.get(i), i);
                data.genBlocks.add(new Tuple<>(pos, blocks.get(i).block.defaultBlockState()));
            }

            this.message = Component.literal(x + "/" + mapW);
        }
    }

    /**
     * 优化阶梯高度，在方向变化时截断阶梯以压缩整体高度
     */
    private List<Integer> optimizeStaircaseHeights(List<Integer> rawHeights) {
        if (rawHeights.isEmpty()) return new ArrayList<>();

        List<Integer> optimizedHeights = new ArrayList<>();
        int currentHeight = 0;
        int lastDirection = 0; // -1: 下降, 0: 平坦, 1: 上升

        optimizedHeights.add(currentHeight);

        for (int i = 1; i < rawHeights.size(); i++) {
            int heightDiff = rawHeights.get(i);
            int direction = Integer.compare(heightDiff, 0);

            // 检查方向是否发生变化
            if (lastDirection != 0 && direction != 0 && lastDirection != direction) {
                // 方向变化，截断阶梯
                currentHeight = 0; // 重置高度基准
            } else {
                // 方向未变化或从平坦状态变化，正常累加
                currentHeight += heightDiff;
            }

            optimizedHeights.add(currentHeight);

            // 更新方向记录（只记录非零方向）
            if (direction != 0) {
                lastDirection = direction;
            }
        }

        // 将所有高度调整为非负数
        int minHeight = optimizedHeights.stream().min(Integer::compare).orElse(0);
        if (minHeight < 0) {
            for (int i = 0; i < optimizedHeights.size(); i++) {
                optimizedHeights.set(i, optimizedHeights.get(i) - minHeight);
            }
        }

        return optimizedHeights;
    }
}