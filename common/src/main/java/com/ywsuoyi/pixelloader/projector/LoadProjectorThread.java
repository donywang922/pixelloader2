package com.ywsuoyi.pixelloader.projector;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import com.ywsuoyi.pixelloader.PixelLoader;
import com.ywsuoyi.pixelloader.colorspace.ColorSpace;
import com.ywsuoyi.pixelloader.colorspace.ColoredBlock;
import com.ywsuoyi.pixelloader.loadingThreadUtil.LoadingThread;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
            float h = step * height / 2;

            Pair<Matrix4f, Vec3> pair = ProjectorSetting.ToM4f(data.center, setting);
            Matrix4f m4f = pair.getFirst();
            Vec3 vec3From = pair.getSecond();
            for (int y = read.getMinY(); y < height; y += 5) {
                for (int x = read.getMinX(); x < width; x += 5) {
                    if (state == State.end) {
                        setting.state = ProjectorSetting.LoadState.Error;
                        onend(true);
                        return;
                    }
                    Vector4f v4fTo = new Vector4f(1f - step * x, h - step * y, (float) (setting.scale + 5) / 16, 1.0f);
                    v4fTo.transform(m4f);
                    Vec3 vec3To = new Vec3(v4fTo.x(), v4fTo.y(), v4fTo.z());
                    BlockPos target = BlockGetter.traverseBlocks(vec3From, vec3To, null, (con, pos) -> {
                        BlockState blockState = level.getBlockState(pos);
                        return blockState.getCollisionShape(level, pos).isEmpty() ? null : pos;
                    }, con -> null);
                    if (target != null) {
                        if (!setting.genBlocks.containsKey(target)) {
                            ColoredBlock block = ColorSpace.blockSpace.getBlockTree(calcRGB(read.getRGB(x, y)));
                            r -= block.r;
                            g -= block.g;
                            b -= block.b;
                            setting.genBlocks.put(target, block.block.defaultBlockState());
                        }
                    }
                }
                setting.message = Component.literal(y + "/" + height);
            }
            setting.state = ProjectorSetting.LoadState.Finish;
        } catch (IOException e) {
            PixelLoader.logger.error("Failed to generate image: {}", e.getMessage());
            setting.state = ProjectorSetting.LoadState.Error;
        }
        onend(false);
    }
}
