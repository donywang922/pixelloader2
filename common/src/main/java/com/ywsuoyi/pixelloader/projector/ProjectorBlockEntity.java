package com.ywsuoyi.pixelloader.projector;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import com.ywsuoyi.pixelloader.PixelLoader;
import com.ywsuoyi.pixelloader.Setting;
import com.ywsuoyi.pixelloader.colorspace.ColorSpace;
import com.ywsuoyi.pixelloader.loadingThreadUtil.BaseThread;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ProjectorBlockEntity extends BlockEntity {
    public ProjectorSetting setting;
    public int tick = 0;
    public List<Tuple<BlockPos, BlockState>> blocks = new ArrayList<>();


    public ProjectorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(PixelLoader.projectorBlockEntity, blockPos, blockState);
        setting = ProjectorSetting.get(blockPos);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ProjectorBlockEntity entity) {
        ProjectorSetting set = entity.setting;
        if (set.state == ProjectorSetting.LoadState.Placing) {
            set.message = Component.translatable("pixelLoader.waiting");
            set.genBlocks.forEach((pos1, state1) -> {
                if (FallingBlock.isFree(level.getBlockState(pos1.below())))
                    level.setBlock(pos1.below(), Blocks.GLASS.defaultBlockState(), 3);
                level.setBlock(pos1, state1, 3);
            });
            set.state = ProjectorSetting.LoadState.Done;
            return;
        }
        if (set.state == ProjectorSetting.LoadState.WaitStart) {
            if (!ColorSpace.allLoad()) {
                set.state = ProjectorSetting.LoadState.Select;
                set.message = Component.translatable("pixelLoader.colored_block.needload");
                return;
            }
            if (Setting.imglist.isEmpty()) {
                set.state = ProjectorSetting.LoadState.Select;
                set.message = Component.translatable("pixelLoader.noFile");
                return;
            }
            BaseThread.addThread(new LoadProjectorThread(
                    null,
                    Setting.getImg(),
                    Setting.fs,
                    Setting.imgSize,
                    Setting.cutout,
                    level,
                    pos
            ));
        }
    }

    public static void renderTick(Level level, BlockPos pos, BlockState state, ProjectorBlockEntity entity) {
        entity.tick++;
        ProjectorSetting set = entity.setting;
        if (set.state == ProjectorSetting.LoadState.Select && set.changed && entity.tick % 5 == 0) {
            set.outLinePos.clear();
            Pair<Matrix4f, Vec3> pair = ProjectorSetting.ToM4f(pos, set);
            Matrix4f m4f = pair.getFirst();
            Vec3 vec3From = pair.getSecond();
            for (int i = 0; i < set.width; i += 20) {
                float h = 2f / set.width * set.height;
                Vec3 vec3To = toVec3(-1f + (2f / set.width) * i, -h / 2, (float) (set.scale + 5) / 16, m4f);
                BlockHitResult hitResult = level.clip(new ClipContext(vec3From, vec3To, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null));
                if (hitResult.getType() != HitResult.Type.MISS)
                    set.outLinePos.add(hitResult.getBlockPos());
                vec3To = toVec3(-1f + (2f / set.width) * i, h / 2, (float) (set.scale + 5) / 16, m4f);
                hitResult = level.clip(new ClipContext(vec3From, vec3To, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null));
                if (hitResult.getType() != HitResult.Type.MISS)
                    set.outLinePos.add(hitResult.getBlockPos());
            }
            set.changed = false;
        }
        if ((set.state == ProjectorSetting.LoadState.Start || set.state == ProjectorSetting.LoadState.Finish) && entity.tick % 40 == 0) {
            entity.blocks.clear();
            set.genBlocks.forEach((blockPos, blockState) -> entity.blocks.add(new Tuple<>(blockPos.subtract(pos), blockState)));
        }
    }

    public static Vec3 toVec3(float x, float y, float z, Matrix4f m4f) {
        Vector4f v4fTo = new Vector4f(x, y, z, 1.0f);
        v4fTo.transform(m4f);
        return new Vec3(v4fTo.x(), v4fTo.y(), v4fTo.z());
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        compoundTag.putDouble("roll", setting.roll);
        compoundTag.putDouble("yaw", setting.yaw);
        compoundTag.putDouble("pitch", setting.pitch);
        compoundTag.putDouble("scale", setting.scale);

    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        setting.roll = compoundTag.getDouble("roll");
        setting.yaw = compoundTag.getDouble("yaw");
        setting.pitch = compoundTag.getDouble("pitch");
        setting.scale = compoundTag.getDouble("scale");
        setChanged();
    }


}
