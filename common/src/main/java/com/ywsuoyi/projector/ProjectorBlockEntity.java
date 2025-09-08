package com.ywsuoyi.projector;

import com.mojang.datafixers.util.Pair;
import com.ywsuoyi.PixelLoader;
import com.ywsuoyi.colorspace.ColorSpaces;
import com.ywsuoyi.loadingThreadUtil.BaseThread;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class ProjectorBlockEntity extends BlockEntity {
    public ProjectorSetting setting;
    public int tick = 0;


    public ProjectorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(PixelLoader.projectorBlockEntity, blockPos, blockState);
        setting = ProjectorSetting.get(blockPos);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ProjectorBlockEntity entity) {
        ProjectorSetting set = entity.setting;
        if (set.state == ProjectorSetting.LoadState.Placing) {
            set.message = Component.translatable("pixelLoader.projector.screen.waiting");
            set.genBlocks.forEach((tuple) -> {
                if (tuple.getB().isAir()) return;
                if (FallingBlock.isFree(level.getBlockState(tuple.getA().below())))
                    level.setBlock(tuple.getA().below(), Blocks.GLASS.defaultBlockState(), 3);
                level.setBlock(tuple.getA(), tuple.getB(), 3);
            });
            set.state = ProjectorSetting.LoadState.Done;
            return;
        }
        if (set.state == ProjectorSetting.LoadState.WaitStart) {
            if (!ColorSpaces.allLoad()) {
                set.state = ProjectorSetting.LoadState.Select;
                set.message = Component.translatable("pixelLoader.needload");
                return;
            }
            if (set.getImg() == null) {
                set.state = ProjectorSetting.LoadState.Select;
                set.message = Component.translatable("pixelLoader.projector.screen.noFile");
                return;
            }
            set.thread = new LoadProjectorThread(
                    set.getImg(),
                    set.dither,
                    set.cutout,
                    level,
                    pos
            );
            BaseThread.addThread(set.thread);
        }
    }

    public static void renderTick(Level level, BlockPos pos, BlockState state, ProjectorBlockEntity entity) {
        entity.tick++;
        ProjectorSetting set = entity.setting;
        if (set.state == ProjectorSetting.LoadState.Select && set.changed && entity.tick % 5 == 0) {
            set.latticePos.clear();
            Pair<Matrix4f, Vec3> pair = ProjectorSetting.ToM4f(pos, set);
            Matrix4f m4f = pair.getFirst();
            Vec3 vec3From = pair.getSecond();
            set.scale = Math.max(set.scale, 1);
            float px = 2f / set.width;
            float h = px * set.height / 2;
            float z = (float) (set.scale + 5) / 16;

            int step = set.width / 25;
            for (int j = 0; j < set.height; j += step) {
                for (int i = 0; i < set.width; i += step) {
                    tryAddOutLine(-1f + px * i, -h + px * j, z, m4f, vec3From, set.latticePos, level);
                }
            }
            set.changed = false;
        }
    }

    public static Vec3 toVec3(float x, float y, float z, Matrix4f m4f) {
        Vector4f v4fTo = new Vector4f(x, y, z, 1.0f);
        v4fTo.mul(m4f);
        return new Vec3(v4fTo.x(), v4fTo.y(), v4fTo.z());
    }

    public static void tryAddOutLine(float x, float y, float z, Matrix4f m4f, Vec3 vec3From, ArrayList<BlockPos> set, Level level) {
        Vec3 vec3To = toVec3(x, y, z, m4f);
        BlockPos target = BlockGetter.traverseBlocks(vec3From, vec3To, null, (con, pos) -> {
            BlockState blockState = level.getBlockState(pos);
            return blockState.getCollisionShape(level, pos).isEmpty() ? null : pos;
        }, con -> null);
        set.add(target);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        compoundTag.putDouble("roll", setting.roll);
        compoundTag.putDouble("yaw", setting.yaw);
        compoundTag.putDouble("pitch", setting.pitch);
        compoundTag.putDouble("scale", setting.scale);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        setting.roll = compoundTag.getDouble("roll");
        setting.yaw = compoundTag.getDouble("yaw");
        setting.pitch = compoundTag.getDouble("pitch");
        setting.scale = compoundTag.getDouble("scale");
        setChanged();
    }
}
