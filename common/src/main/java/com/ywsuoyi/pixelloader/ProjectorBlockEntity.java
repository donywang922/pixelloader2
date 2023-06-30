package com.ywsuoyi.pixelloader;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ProjectorBlockEntity extends BlockEntity {
    public ProjectorSetting setting;
    public int tick = 0;


    public ProjectorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(PixelLoader.projectorBlockEntity, blockPos, blockState);
        setting = ProjectorSetting.get(blockPos);
    }

    public static void renderTick(Level level, BlockPos pos, BlockState state, ProjectorBlockEntity entity) {
        ProjectorSetting set = entity.setting;
        if (set.changed && entity.tick % 5 == 0) {
            set.outLinePos.clear();
            Matrix4f m4f = Matrix4f.createTranslateMatrix(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
            m4f.multiply(Vector3f.YP.rotationDegrees((float) set.yaw));
            m4f.multiply(Vector3f.XP.rotationDegrees((float) set.pitch));
            m4f.multiply(Vector3f.ZP.rotationDegrees((float) set.roll));
            Vector4f v4fFrom = new Vector4f(0, 0, 0, 1.0f);
            v4fFrom.transform(m4f);
            Vec3 vec3From = new Vec3(v4fFrom.x(), v4fFrom.y(), v4fFrom.z());
            m4f.multiply(Matrix4f.createScaleMatrix(256, 256, 256));

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
        entity.tick++;
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
