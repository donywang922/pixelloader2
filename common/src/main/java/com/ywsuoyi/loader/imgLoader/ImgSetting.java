package com.ywsuoyi.loader.imgLoader;

import com.ywsuoyi.ImageManager;
import com.ywsuoyi.Selections;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImgSetting {
    public static int index = 0;

    public static int dither = 1;
    public static int cutout = 0;
    public static int support = 0;
    public static int cover = 0;
    public static int onfinish=0;

    public static int imgWidth = 0;
    public static int imgHeight = 0;
    public static int genWidth = 0;
    public static int genHeight = 0;
    public static float genScale = 0.125f;
    public static Keep keep = Keep.scale;

    public static int preset = 0;
    public static String xExpr;
    public static String yExpr;
    public static String zExpr;


    public static File getImg() {
        return ImageManager.getImg(index);
    }

    public static void addindex(Player player) {
        index++;
        if (index > ImageManager.imglist.size() - 1) {
            index = 0;
            ImageManager.updateFileList();
        }
        if (player != null) {
            if (!ImageManager.hasImg())
                player.displayClientMessage(Component.translatable("pixelLoader.fileNotFind"), true);
            else
                player.displayClientMessage(Component.translatable("pixelLoader.selectFile", ImageManager.getImgName(index)), true);
        }
    }

    public static void reload() {
        File img = getImg();
        if (img == null) return;
        try {
            BufferedImage read = ImageIO.read(img);
            imgWidth = read.getWidth();
            imgHeight = read.getHeight();
        } catch (IOException ignored) {
        }
    }

    public static float getScale() {
        return switch (ImgSetting.keep) {
            case width -> ImgSetting.genWidth / (float) imgWidth;
            case height -> ImgSetting.genHeight / (float) imgHeight;
            case scale -> ImgSetting.genScale;
        };
    }

    public static CoordinateSystem getCoordinateSystem() {
        return new CoordinateSystem(xExpr, yExpr, zExpr);
    }

    static {
        List<String> functions = Selections.presets.getFirst();
        xExpr = functions.getFirst();
        yExpr = functions.get(1);
        zExpr = functions.get(2);
    }

    public enum Keep {
        width, height, scale
    }
}
