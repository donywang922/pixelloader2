package com.ywsuoyi.pixelloader;

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
    public static File imgFolder = new File("./img");
    public static final List<File> imglist = new ArrayList<>();
    public static int index = 0;
    public static final NonNullList<ItemStack> banItem = NonNullList.withSize(54, ItemStack.EMPTY);

    public static boolean dither = true;
    public static int cutout = 0;

    public static int mapSize = 1;

    public static MapMode mapMode = MapMode.threeD;

    public static int imgSize = 8;
    public static boolean flat = true;


    public static File getImg() {
        return imglist.get(index);
    }

    public static void addindex(Player player) {
        index++;
        if (index > imglist.size() - 1) {
            index = 0;
            updateFileList();
        }
        if (player != null) {
            if (imglist.isEmpty())
                player.displayClientMessage(Component.translatable("pixelLoader.fileNotFind"), true);
            else
                player.displayClientMessage(Component.translatable("pixelLoader.selectFile", imglist.get(index).getName()), true);
        }
    }

    public static void updateFileList() {
        imglist.clear();
        try (Stream<Path> list = Files.list(Paths.get("img/"))) {
            list.filter(f ->
                    {
                        String n = f.getFileName().toString().toLowerCase();
                        return !Files.isDirectory(f) && (n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png"));
                    })
                    .forEach(path -> {
                        if (Files.isDirectory(path))
                            return;
                        imglist.add(path.toFile());
                    });
        } catch (IOException e) {
            PixelLoader.logger.error("Failed to update file list: {}", e.getMessage());
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
