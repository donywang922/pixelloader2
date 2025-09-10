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
    public static int onfinish = 0;

    public static int imgWidth = 0;
    public static int imgHeight = 0;
    public static int zRange = 0;
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
        File selectedFile = ImageManager.getImg(ImgSetting.index);
        if (selectedFile == null) {
            // 没有选中的文件
            imgWidth = 1;
            imgHeight = 1;
            zRange = 0;
            return;
        }

        if (selectedFile.isDirectory()) {
            // 文件夹模式
            List<File> imageFiles = ImageManager.getAllImagesFromDirectory(selectedFile);
            zRange = imageFiles.size() - 1;

            // 使用第一张图片的尺寸
            if (!imageFiles.isEmpty()) {
                File firstImage = imageFiles.get(0);
                try {
                    BufferedImage img = ImageIO.read(firstImage);
                    if (img != null) {
                        ImgSetting.imgWidth = img.getWidth();
                        ImgSetting.imgHeight = img.getHeight();
                    } else {
                        // 图片读取失败，使用默认值
                        ImgSetting.imgWidth = 1;
                        ImgSetting.imgHeight = 1;
                    }
                } catch (IOException e) {
                    // 读取错误，使用默认值
                    ImgSetting.imgWidth = 1;
                    ImgSetting.imgHeight = 1;
                }
            } else {
                // 文件夹为空
                imgWidth = 1;
                imgHeight = 1;
                zRange = 0;
            }
        } else {
            // 单个图片文件模式
            ImgSetting.zRange = 0;

            try {
                BufferedImage img = ImageIO.read(selectedFile);
                if (img != null) {
                    ImgSetting.imgWidth = img.getWidth();
                    ImgSetting.imgHeight = img.getHeight();
                } else {
                    // 图片读取失败，使用默认值
                    ImgSetting.imgWidth = 1;
                    ImgSetting.imgHeight = 1;
                }
            } catch (IOException e) {
                // 读取错误，使用默认值
                ImgSetting.imgWidth = 1;
                ImgSetting.imgHeight = 1;
            }
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
        CoordinateSystem coordinateSystem = new CoordinateSystem(xExpr, yExpr, zExpr);
        coordinateSystem.getWorldCoords(0, 0, genWidth, genHeight, 0);
        coordinateSystem.getWorldCoords(genWidth, genHeight, genWidth, genHeight, zRange);
        return coordinateSystem;
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
