package com.ywsuoyi.projector;

import com.mojang.math.Axis;
import net.minecraft.util.Tuple;
import org.joml.Matrix4f;
import com.ywsuoyi.PixelLoader;
import com.ywsuoyi.Vec2i;
import com.ywsuoyi.colorspace.ColorSpaces;
import com.ywsuoyi.colorspace.ColoredBlock;
import com.ywsuoyi.loadingThreadUtil.LoadingThread;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.joml.Vector4f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ArrayList;

import static com.ywsuoyi.PixelLoader.neb;

public class LoadProjectorThread extends LoadingThread {
    public ProjectorSetting setting;

    public LoadProjectorThread(File file, int dither, int cutout, Level level, BlockPos anchor) {
        super(null, file, dither, cutout, level, anchor, anchor);
        setting = ProjectorSetting.get(anchor);
        setting.message = Component.translatable("pixelLoader.projector.screen.waiting");
        setting.state = ProjectorSetting.LoadState.Start;
    }

    /**
     * 计算给定网格索引位置的最大搜索深度
     *
     * @param latticeIndex 在latticePos中的索引
     * @param latticePos   网格位置列表
     * @param gridWidth    网格宽度（25）
     * @param gridHeight   网格高度
     * @return 计算出的最大搜索深度
     */
    private int calculateMaxDepth(int latticeIndex, ArrayList<BlockPos> latticePos, int gridWidth, int gridHeight) {
        int row = latticeIndex / gridWidth;
        int col = latticeIndex % gridWidth;

        BlockPos currentPos = latticePos.get(latticeIndex);
        if (currentPos == null) {
            return (int) (20 * setting.scale); // 如果当前位置为null，使用较大的默认值
        }

        int maxDistance = 0;
        boolean hasNullNeighbor = false;

        // 检查周围8个方向的邻居
        int[] dRow = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dCol = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            int newRow = row + dRow[i];
            int newCol = col + dCol[i];

            // 检查边界
            if (newRow < 0 || newRow >= gridHeight || newCol < 0 || newCol >= gridWidth) {
                continue;
            }

            int neighborIndex = newRow * gridWidth + newCol;
            if (neighborIndex >= latticePos.size()) {
                continue;
            }

            BlockPos neighborPos = latticePos.get(neighborIndex);
            if (neighborPos == null) {
                hasNullNeighbor = true;
            } else {
                // 计算曼哈顿距离
                int manhattanDistance = Math.abs(currentPos.getX() - neighborPos.getX()) +
                        Math.abs(currentPos.getY() - neighborPos.getY()) +
                        Math.abs(currentPos.getZ() - neighborPos.getZ());
                maxDistance = Math.max(maxDistance, manhattanDistance);
            }
        }

        // 如果周围有null点，按最大距离*2；否则使用最大距离
        int calculatedDepth = hasNullNeighbor ? maxDistance : maxDistance / 2;

        // 设置合理的最小值和最大值
        calculatedDepth = Math.min(calculatedDepth, (int) (10 * setting.scale));

        return calculatedDepth;
    }

    /**
     * 判断方块的某个面是否对anchor位置可见
     * @param blockPos 方块位置
     * @param face 方块的面方向
     * @param anchorPos anchor位置（通常是投影仪位置）
     * @return 如果面对anchor可见返回true
     */
    private boolean isFaceVisibleFromAnchor(BlockPos blockPos, Direction face, BlockPos anchorPos) {
        return switch (face) {
            case NORTH -> // 北面 (负Z方向)
                    anchorPos.getZ() < blockPos.getZ();
            case SOUTH -> // 南面 (正Z方向)
                    anchorPos.getZ() > blockPos.getZ();
            case WEST ->  // 西面 (负X方向)
                    anchorPos.getX() < blockPos.getX();
            case EAST ->  // 东面 (正X方向)
                    anchorPos.getX() > blockPos.getX();
            case DOWN ->  // 下面 (负Y方向)
                    anchorPos.getY() < blockPos.getY();
            case UP ->    // 上面 (正Y方向)
                    anchorPos.getY() > blockPos.getY();
        };
    }

    @Override
    public void run() {
        if (file == null) {
            onend(false);
            return;
        }
        try {
            BufferedImage read = ImageIO.read(file);

            int width = read.getWidth();
            int height = read.getHeight();

            float step = 2f / width;
            float len = width / 2f;
            float h = step * height / 2;
            float scale = ((float) setting.scale + 5f) / 16f;

            HashMap<Vec2i, LinkedList<BlockPos>> pixelToBlock = new HashMap<>();
            LinkedList<WaitPos> waitForCheck = new LinkedList<>();

            // 计算网格尺寸
            int gridWidth = 25;
            int gridHeight = (setting.latticePos.size() + gridWidth - 1) / gridWidth; // 向上取整
            HashSet<BlockPos> got = new HashSet<>(1024);
            // 为每个网格点添加到待检查队列，使用动态计算的最大深度
            for (int i = 0; i < setting.latticePos.size(); i++) {
                BlockPos pos = setting.latticePos.get(i);
                if (pos != null) {
                    if (got.contains(pos)) continue;
                    int maxDepth = calculateMaxDepth(i, setting.latticePos, gridWidth, gridHeight);
                    waitForCheck.addLast(new WaitPos(pos, 0, maxDepth));
                    got.add(pos);
                }
            }


            Matrix4f routeBack = new Matrix4f().translation(0, 0, 0);
            routeBack.rotate(Axis.ZP.rotationDegrees((float) -setting.roll));
            routeBack.rotate(Axis.XP.rotationDegrees((float) -setting.pitch));
            routeBack.rotate(Axis.YP.rotationDegrees((float) -setting.yaw));

            got.clear();
            int tc = 0;
            while (!waitForCheck.isEmpty()) {
                if (state == State.end) {
                    setting.state = ProjectorSetting.LoadState.Error;
                    onend(true);
                    return;
                }
                WaitPos wp = waitForCheck.removeFirst();
                BlockPos pos = wp.pos;
                BlockPos pos2 = pos.subtract(data.center);
                Vector4f v4f = new Vector4f(pos2.getX(), pos2.getY(), pos2.getZ(), 1);
                v4f.mul(routeBack);
                v4f.mul(scale / v4f.z());
                if (v4f.x() > 1 || v4f.x() < -1 || v4f.y() > h || v4f.y() < -h) continue;
                Vec2i pixel = new Vec2i((int) ((v4f.x() + 1) * len), (int) ((v4f.y() + h) * len));
                if (!pixelToBlock.containsKey(pixel)) {
                    pixelToBlock.put(pixel, new LinkedList<>());
                    tc++;
                }
                pixelToBlock.get(pixel).addFirst(pos);

                // 使用动态计算的最大深度
                if (wp.dep > wp.maxDepth) continue;

                for (int i = 0; i < 26; i++) {
                    BlockPos pos1 = pos.offset(neb[i]);
                    if (got.contains(pos1)) continue;
                    got.add(pos1);
                    if (level.getBlockState(pos1).getCollisionShape(level, pos1).isEmpty()) continue;
                    for (Direction direction : Direction.values()) {
                        if (!isFaceVisibleFromAnchor(pos1, direction, anchor)) continue;
                        BlockPos offset = pos1.offset(direction.getNormal());
                        if (level.getBlockState(offset).getCollisionShape(level, offset).isEmpty()) {
                            waitForCheck.addLast(new WaitPos(pos1, wp.dep + 1, wp.maxDepth));
                            break;
                        }
                    }
                }
                setting.message = Component.literal("0/" + tc);
            }
            int cc = 0;
            for (int y = read.getMinY(); y < height; y++) {
                for (int x = read.getMinX(); x < width; x++) {
                    if (state == State.end) {
                        setting.state = ProjectorSetting.LoadState.Error;
                        onend(true);
                        return;
                    }
                    Vec2i pixel = new Vec2i(x, y);
                    if (!pixelToBlock.containsKey(pixel)) continue;
                    for (BlockPos pos : pixelToBlock.get(pixel)) {
                        ColoredBlock block = ColorSpaces.blockSpace.getBlock(calcRGB(read.getRGB(width - x - 1, height - y - 1)));
                        r -= block.r;
                        g -= block.g;
                        b -= block.b;
                        setting.genBlocks.add(new Tuple<>(pos, block.block.defaultBlockState()));
                        cc++;
                    }
                }
                setting.message = Component.literal(cc + "/" + tc);
            }
            setting.state = ProjectorSetting.LoadState.Finish;
        } catch (IOException e) {
            PixelLoader.logger.error("Failed to generate image: {}", e.getMessage());
            setting.state = ProjectorSetting.LoadState.Error;
        }

        onend(false);
    }

    public static class WaitPos {
        BlockPos pos;
        int dep;
        int maxDepth;

        public WaitPos(BlockPos pos, int dep, int maxDepth) {
            this.pos = pos;
            this.dep = dep;
            this.maxDepth = maxDepth;
        }
    }
}