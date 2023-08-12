package com.ywsuoyi.pixelloader;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectorSetting {
    public static final HashMap<BlockPos, ProjectorSetting> settings = new HashMap<>();

    public double roll = 0;
    public double yaw = 0;
    public double pitch = 0;
    public double scale = 15;

    public int width = 0;
    public int height = 0;
    public boolean fs = true;

    public File img;

    public int fileIndex = 0;

    public boolean editing = false;

    public boolean changed = true;

    public LoadState state = LoadState.Select;
    public LoadProjectorThread thread;
    public NonNullList<BlockPos> outLinePos = NonNullList.create();

    public ConcurrentHashMap<BlockPos, BlockState> genBlocks = new ConcurrentHashMap<>();

    public Component message = Component.empty();

    public ProjectorSetting() {
        loadimg();
    }

    public Component getFileText() {

        if (Setting.imglist.isEmpty()) {
            return Component.translatable("pixelLoader.fileNotFind");
        }
        if (img == null)
            loadimg();
        return Component.translatable("pixelLoader.selectFile", img.getName());
    }

    public void addindex() {
        if (state == LoadState.Select) {
            fileIndex++;
            if (fileIndex > Setting.imglist.size() - 1) {
                fileIndex = 0;
                Setting.updateFileList();
            }
            loadimg();
            changed = true;
        }
    }

    private void loadimg() {
        if (!Setting.imglist.isEmpty()) {
            img = Setting.imglist.get(fileIndex);
            if (img == null) {
                fileIndex = 0;
                img = Setting.imglist.get(fileIndex);
            }
            try {
                BufferedImage read = ImageIO.read(img);
                width = read.getWidth();
                height = read.getHeight();
            } catch (IOException ignored) {
            }
        }
    }


    public void setRound(double roll, double yaw, double pitch, double scale) {
        this.roll = Math.round(roll * 1000) / 1000.0;
        this.yaw = Math.round(yaw * 1000) / 1000.0;
        this.pitch = Math.round(pitch * 1000) / 1000.0;
        this.scale = Math.round(scale * 1000) / 1000.0;
    }

    public void set(double roll, double yaw, double pitch, double scale) {
        this.roll = roll;
        this.yaw = yaw;
        this.pitch = pitch;
        this.scale = scale;
    }

    public void set(String roll, String yaw, String pitch, String scale, Player player) {
        set(roll, yaw, pitch, scale);
        player.setXRot((float) this.pitch + 0.0000001f);
        player.setYRot((float) -this.yaw + 0.0000001f);
    }

    public void set(String roll, String yaw, String pitch, String scale) {
        this.roll = tryParse(roll);
        this.yaw = tryParse(yaw);
        this.pitch = tryParse(pitch);
        this.scale = tryParse(scale);
        changed = true;
    }

    public static double tryParse(String num) {
        try {
            return Double.parseDouble(num);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    public static ProjectorSetting get(BlockPos pos) {
        if (ProjectorSetting.settings.containsKey(pos)) return ProjectorSetting.settings.get(pos);
        else {
            ProjectorSetting setting = new ProjectorSetting();
            settings.put(pos, setting);
            return setting;
        }
    }

    public static Pair<Matrix4f, Vec3> ToM4f(BlockPos pos, ProjectorSetting set) {
        Matrix4f m4f = Matrix4f.createTranslateMatrix(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
        m4f.multiply(Vector3f.YP.rotationDegrees((float) set.yaw));
        m4f.multiply(Vector3f.XP.rotationDegrees((float) set.pitch));
        m4f.multiply(Vector3f.ZP.rotationDegrees((float) set.roll));
        Vector4f v4fFrom = new Vector4f(0, 0, 0, 1.0f);
        v4fFrom.transform(m4f);
        Vec3 vec3From = new Vec3(v4fFrom.x(), v4fFrom.y(), v4fFrom.z());
        float f = (float) (256d * (set.scale + 5d) / 16d);
        m4f.multiply(Matrix4f.createScaleMatrix(f, f, f));
        return new Pair<>(m4f, vec3From);
    }

    public enum LoadState {
        Select,
        WaitStart,
        Start,
        Finish,
        Placing,
        Done,
        Error
    }

    @Override
    public String toString() {
        return "ProjectorSetting{" +
                "roll=" + roll +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                ", scale=" + scale +
                '}';
    }
}
