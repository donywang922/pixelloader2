package com.ywsuoyi.loader.mapLoader;

import com.ywsuoyi.PixelLoader;
import com.ywsuoyi.Vec2i;
import com.ywsuoyi.colorspace.AbstractColorSpace;
import com.ywsuoyi.colorspace.ColorSpaces;
import com.ywsuoyi.colorspace.ColoredBlock;
import com.ywsuoyi.loadingThreadUtil.LoadingThread;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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

    public LoadMapThread(Player player, File file, int dither, int cutout, int support, int cover, int finish, Level level, BlockPos anchor, int size, MapSetting.MapMode mode, int fit) {
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
        if (file == null) {
            onend(false);
            return;
        }
        try {
            BufferedImage read = ImageIO.read(file);
            int width = read.getWidth(), height = read.getHeight();

            // 计算地图尺寸
            int mapW, mapH;
            if (fit == 0) {
                // 缩小图片到地图中
                float border = Math.max(width, height);
                mapW = Math.round(width / border * 128 * size);
                mapH = Math.round(height / border * 128 * size);
            } else {
                // 放大图片以覆盖地图，裁切多余部分
                mapW = 128 * size;
                mapH = 128 * size;
            }

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

        processFinish("_map");
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

                Vec2i result = getPixel(width, height, mapW, mapH, x, y);

                ColoredBlock block;
                int rgb = read.getRGB(result.x, result.y);
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
            this.message = Component.translatable("pixelLoader.LoadingThread.gen", x + "/" + mapW);
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
                Vec2i result = getPixel(width, height, mapW, mapH, x, y);

                ColoredBlock block = space.getBlock(calcRGB(read.getRGB(result.x, result.y)));
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

            this.message = Component.translatable("pixelLoader.LoadingThread.gen", x + "/" + mapW);
        }
    }

    public Vec2i getPixel(int width, int height, int mapW, int mapH, int x, int y) {
        int px, py;
        if (fit == 0) {
            // 缩小模式：按比例采样
            px = Math.round((float) width / mapW * x);
            py = Math.round((float) height / mapH * y);
        } else {
            // 放大模式：居中裁切
            float scaleX = (float) width / mapW;
            float scaleY = (float) height / mapH;
            float scale = Math.min(scaleX, scaleY);

            // 计算居中偏移
            int scaledWidth = Math.round(mapW * scale);
            int scaledHeight = Math.round(mapH * scale);
            int offsetX = (width - scaledWidth) / 2;
            int offsetY = (height - scaledHeight) / 2;

            px = Math.min(width - 1, Math.max(0, offsetX + Math.round(x * scale)));
            py = Math.min(height - 1, Math.max(0, offsetY + Math.round(y * scale)));
        }
        return new Vec2i(px, py);
    }

    /**
     * 优化阶梯高度，在方向变化时截断阶梯以压缩整体高度
     */
    private List<Integer> optimizeStaircaseHeights(List<Integer> rawHeights) {
        if (rawHeights.isEmpty()) return new ArrayList<>();

        LinkedList<Integer> vShape = new LinkedList<>();
        int flat = 0;
        LinkedList<Shape> shapes = new LinkedList<>();
        int lastDirection = -1; // -1: 下降, 0: 平坦, 1: 上升
        int currentHeight = 0;

        for (int i = 0; i < rawHeights.size(); i++) {
            int heightDiff = rawHeights.get(i);
            if (heightDiff == 0) {
                if (flat == 0 && !vShape.isEmpty()) {
                    vShape.pollLast();
                    flat++;
                }
                flat++;
            } else {
                if (heightDiff != lastDirection && lastDirection == 1) {
                    if (flat == 0) {
                        flat++;
                        vShape.pollLast();
                    }
                    shapes.add(new Shape(vShape));
                    shapes.add(new Shape(flat));
                    vShape = new LinkedList<>();
                    flat = 0;
                } else if (flat != 0) {
                    for (int j = 0; j < flat; j++) {
                        vShape.add(currentHeight);
                    }
                    flat = 0;
                }
                currentHeight += heightDiff;
                lastDirection = heightDiff;
                vShape.add(currentHeight);
            }
        }
        for (int j = 0; j < flat; j++) {
            vShape.add(currentHeight);
        }
        if (!vShape.isEmpty())
            shapes.add(new Shape(vShape));

        LinkedList<Integer> optimizedHeights = new LinkedList<>(shapes.pollFirst().data);
        while (!shapes.isEmpty()) {
            Shape shape = shapes.pollFirst();
            if (shape.flat) {
                Shape next = shapes.peekFirst();
                int hOffset = Math.max(next == null ? 0 : next.data.peekFirst() + 1, optimizedHeights.peekLast() + 1);
                shape.data.forEach(h -> {
                    optimizedHeights.add(h + hOffset);
                });
                continue;
            } else {
                optimizedHeights.addAll(shape.data);
            }
        }
        return optimizedHeights;
    }

    public static class Shape {
        boolean flat;
        LinkedList<Integer> data;

        public Shape(LinkedList<Integer> data) {
            this.flat = false;
            int min = data.stream().min(Integer::compare).orElse(0);
            LinkedList<Integer> tmp = new LinkedList<>();
            data.forEach(h -> tmp.add(h - min));
            this.data = tmp;
        }

        public Shape(int flat1) {
            flat = true;
            data = new LinkedList<>();
            for (int i = 0; i < flat1; i++) {
                data.add(0);
            }
        }
    }
}