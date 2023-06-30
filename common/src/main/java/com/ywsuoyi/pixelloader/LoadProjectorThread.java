package com.ywsuoyi.pixelloader;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LoadProjectorThread extends LoadingThread {
    public ProjectorSetting setting;
    public BlockPos ct;

    public LoadProjectorThread(File file, int no, Level world, ProjectorSetting setting, BlockPos ct) {
        super(file, no, setting.fs, world, null);
        this.setting = setting;
        this.ct = ct;
        setting.message = Component.translatable("pixelLoader.waiting");
        setting.state = ProjectorSetting.LoadState.Start;
        this.blockList = Setting.coloredBlocks;
    }

    @Override
    public void run() {
        try {
            BufferedImage read = ImageIO.read(file);

            int width = read.getWidth();
            int height = read.getHeight();

            float step = 2f / width;
            float h = step * height / 2;

            Pair<Matrix4f, Vec3> pair = ProjectorSetting.ToM4f(ct, setting);
            Matrix4f m4f = pair.getFirst();
            Vec3 vec3From = pair.getSecond();
            for (int y = read.getMinY(); y < height; y += 5) {
                for (int x = read.getMinX(); x < width; x += 5) {
                    if (!run) {
                        setting.state = ProjectorSetting.LoadState.Error;
                        setting.message = Component.translatable("pixelLoader.LoadingThread.stop");
                        Setting.threads.remove(no);
                        Setting.startNextThread();
                        return;
                    }
                    Vector4f v4fTo = new Vector4f(1f - step * x, h - step * y, (float) (setting.scale + 5) / 16, 1.0f);
                    v4fTo.transform(m4f);
                    Vec3 vec3To = new Vec3(v4fTo.x(), v4fTo.y(), v4fTo.z());
                    BlockPos target = BlockGetter.traverseBlocks(vec3From, vec3To, null, (con, pos) -> {
                        BlockState blockState = world.getBlockState(pos);
                        return blockState.getCollisionShape(world, ct).isEmpty() ? null : pos;
                    }, con -> null);
                    if (target != null) {
                        if (!setting.genBlocks.containsKey(target)) {
                            ColoredBlock block = CBlock(Setting.colorBlockMap, CRGB(read.getRGB(x, y)));
                            setting.genBlocks.put(target, block.block.defaultBlockState());
                        }
                    }
                }
                setting.message = Component.literal(y + "/" + height);
            }
            setting.state = ProjectorSetting.LoadState.Finish;
        } catch (IOException e) {
            e.printStackTrace();
            setting.state = ProjectorSetting.LoadState.Error;
            setting.message = Component.translatable("pixelLoader.LoadingThread.stop");
        }

        Setting.threads.remove(no);
        Setting.startNextThread();
        setting.message = Component.translatable("pixelLoader.LoadingThread.finish");
    }
}
