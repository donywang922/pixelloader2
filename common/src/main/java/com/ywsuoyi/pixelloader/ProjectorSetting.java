package com.ywsuoyi.pixelloader;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ProjectorSetting {
    public static final HashMap<BlockPos, ProjectorSetting> settings = new HashMap<>();

    public double roll = 0;
    public double yaw = 0;
    public double pitch = 0;
    public double scale = 15;

    public int width = 0;
    public int height = 0;

    public File img;

    public int fileIndex = 0;

    public boolean editing = false;

    public boolean changed = true;
    public NonNullList<BlockPos> outLinePos = NonNullList.create();

    public ProjectorSetting() {
        loadimg();
    }

    public Component getFileText() {
        if (Setting.imglist.isEmpty()) {
            return Component.translatable("pixelLoader.fileNotFind");
        }
        return Component.translatable("pixelLoader.selectFile", img.getName());
    }

    public void addindex() {
        fileIndex++;
        if (fileIndex > Setting.imglist.size() - 1) {
            fileIndex = 0;
            Setting.updateFileList();
        }
        loadimg();
        changed = true;
    }

    private void loadimg() {
        if (!Setting.imglist.isEmpty()) {
            this.img = Setting.imglist.get(fileIndex);
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
