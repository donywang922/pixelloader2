package com.ywsuoyi.pixelloader.projector;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.ywsuoyi.pixelloader.PixelLoader;
import com.ywsuoyi.pixelloader.Vec2i;
import com.ywsuoyi.pixelloader.colorspace.ColorSpace;
import com.ywsuoyi.pixelloader.colorspace.ColoredBlock;
import com.ywsuoyi.pixelloader.loadingThreadUtil.LoadingThread;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import static com.ywsuoyi.pixelloader.PixelLoader.neb;

public class LoadProjectorThread extends LoadingThread {
    public ProjectorSetting setting;

    public LoadProjectorThread(Player player, File file, boolean dither, int size, int cutout, Level level, BlockPos anchor) {
        super(player, file, dither, size, cutout, level, anchor, anchor);
        setting = ProjectorSetting.get(anchor);
        setting.message = Component.translatable("pixelLoader.waiting");
        setting.state = ProjectorSetting.LoadState.Start;
    }

    @Override
    public void run() {
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
            for (BlockPos pos : setting.latticePos) {
                waitForCheck.addLast(new WaitPos(pos, 0));
            }
            HashSet<BlockPos> got = new HashSet<>();

            Matrix4f routeBack = Matrix4f.createTranslateMatrix(0, 0, 0);
            routeBack.multiply(Vector3f.ZP.rotationDegrees((float) -setting.roll));
            routeBack.multiply(Vector3f.XP.rotationDegrees((float) -setting.pitch));
            routeBack.multiply(Vector3f.YP.rotationDegrees((float) -setting.yaw));

            int tc = 0;
            while (!waitForCheck.isEmpty()) {
                if (state == State.end) {
                    setting.state = ProjectorSetting.LoadState.Error;
                    onend(true);
                    return;
                }
                WaitPos wp = waitForCheck.removeFirst();
                BlockPos pos = wp.pos;
                if (got.contains(pos)) continue;
                got.add(pos);
                BlockPos pos2 = pos.subtract(data.center);
                Vector4f v4f = new Vector4f(pos2.getX(), pos2.getY(), pos2.getZ(), 1);
                v4f.transform(routeBack);
                v4f.mul(scale / v4f.z());
                if (v4f.x() > 1 || v4f.x() < -1 || v4f.y() > h || v4f.y() < -h) continue;
                Vec2i pixel = new Vec2i((int) ((v4f.x() + 1) * len), (int) ((v4f.y() + h) * len));
                if (!pixelToBlock.containsKey(pixel)) {
                    pixelToBlock.put(pixel, new LinkedList<>());
                    tc++;
                }
                pixelToBlock.get(pixel).addFirst(pos);
                if (wp.dep > 10 * scale) continue;

                for (int i = 0; i < 26; i++) {
                    BlockPos pos1 = pos.offset(neb[i]);
                    if (got.contains(pos1)) continue;
                    if (level.getBlockState(pos1).getCollisionShape(level, pos1).isEmpty()) continue;
                    for (Direction direction : Direction.values()) {
                        BlockPos offset = pos1.offset(direction.getNormal());
                        if (level.getBlockState(offset).getCollisionShape(level, offset).isEmpty()) {
                            waitForCheck.addLast(new WaitPos(pos1, wp.dep + 1));
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
                        ColoredBlock block = ColorSpace.blockSpace.getBlock(calcRGB(read.getRGB(width - x - 1, height - y - 1)));
                        r -= block.r;
                        g -= block.g;
                        b -= block.b;
                        setting.genBlocks.put(pos, block.block.defaultBlockState());
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

        public WaitPos(BlockPos pos, int dep) {
            this.pos = pos;
            this.dep = dep;
        }
    }
}
