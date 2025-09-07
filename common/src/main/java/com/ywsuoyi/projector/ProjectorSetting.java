package com.ywsuoyi.projector;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.ywsuoyi.ImageManager;
import com.ywsuoyi.Setting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectorSetting {
    public static final HashMap<BlockPos, ProjectorSetting> settings = new HashMap<>();

    public double roll = 0;
    public double yaw = 0;
    public double pitch = 0;
    public double scale = 15;

    public int width = 0;
    public int height = 0;
    public int cutout;
    public int dither;

    public File img;

    public int fileIndex = 0;

    public boolean editing = false;

    public boolean changed = true;

    public LoadState state = LoadState.Select;
    public LoadProjectorThread thread;
    public ArrayList<BlockPos> latticePos = new ArrayList<>();

    public LinkedList<Tuple<BlockPos, BlockState>> genBlocks = new LinkedList<>();

    public Component message = Component.empty();


    public ProjectorSetting() {
        this.cutout = Setting.cutout;
        this.dither = Setting.dither;
        loadimg(0);
    }

    public File getImg() {
        return ImageManager.getImg(fileIndex);
    }

    public void loadimg() {
        img = ImageManager.getImg(fileIndex);
        if (img == null) {
            fileIndex = 0;
            return;
        }
        try {
            BufferedImage read = ImageIO.read(img);
            width = read.getWidth();
            height = read.getHeight();
            changed = true;
        } catch (IOException ignored) {
        }
    }

    public void loadimg(int i) {
        fileIndex = i;
        loadimg();
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
        this.scale = Math.max(tryParse(scale), 1);
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
        Matrix4f m4f = new Matrix4f().translate(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
        m4f.rotate(Axis.YP.rotationDegrees((float) set.yaw));
        m4f.rotate(Axis.XP.rotationDegrees((float) set.pitch));
        m4f.rotate(Axis.ZP.rotationDegrees((float) set.roll));
        Vector4f v4fFrom = new Vector4f(0, 0, 0, 1.0f);
        v4fFrom.mul(m4f);
        Vec3 vec3From = new Vec3(v4fFrom.x(), v4fFrom.y(), v4fFrom.z());
        float f = (float) (256d * (set.scale + 5d) / 16d);
        m4f.mul(new Matrix4f().scale(f));
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
