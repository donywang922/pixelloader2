package com.ywsuoyi.loader.beaconLoader;

import com.ywsuoyi.ImageManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.io.File;

public class BeaconSetting {
    public static int index = 0;

    public static int dither = 0;
    public static int onfinish = 0;

    public static int size = 64;

    public static File getImg() {
        File selectedFile = ImageManager.getImg(index);
        if (selectedFile == null) return null;

        if (selectedFile.isDirectory()) {
            // 如果选中的是文件夹，返回文件夹中的第一张图片
            return ImageManager.getFirstImageFromDirectory(selectedFile);
        } else {
            // 如果选中的是文件，直接返回
            return selectedFile;
        }
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
}
