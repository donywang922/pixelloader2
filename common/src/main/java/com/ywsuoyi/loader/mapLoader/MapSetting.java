package com.ywsuoyi.loader.mapLoader;

import com.ywsuoyi.ImageManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.io.File;

public class MapSetting {
    public static int index = 0;

    public static int dither = 1;
    public static int cutout = 0;
    public static int support = 0;
    public static int cover = 1;
    public static int onfinish = 0;

    public static int size = 1;
    public static int fit = 0;

    public static MapMode mode = MapMode.threeD;

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


    public enum MapMode {
        flat, threeD, cover, cover_c, cover_c2,
    }
}
