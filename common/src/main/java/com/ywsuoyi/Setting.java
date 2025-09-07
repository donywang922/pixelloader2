package com.ywsuoyi;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Setting {
    public static int index = 0;

    public static int dither = 1;
    public static int cutout = 0;

    public static int mapSize = 1;

    public static MapMode mapMode = MapMode.threeD;

    public static int imgSize = 8;
    public static boolean flat = true;

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
        flat,
        threeD,
        cover,
        cover_c,
        cover_c2,
    }
}
