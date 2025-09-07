package com.ywsuoyi;

import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ImageManager {
    public static File imgFolder = new File("./img");
    public static final List<File> imglist = new ArrayList<>();


    public static List<String> getImageListStr() {
        updateFileList();
        List<String> suggestions = new java.util.ArrayList<>(imglist.stream().map(File::getName).toList());
        if (suggestions.isEmpty()) {
            suggestions.add(Component.translatable("pixelLoader.fileNotFind").toString());
        }
        return suggestions;
    }

    public static File getImg(int i) {
        if (i < 0 || i >= imglist.size()) return null;
        return imglist.get(i);
    }

    public static String getImgName(int i) {
        if (i < 0 || i >= imglist.size()) return "";
        return imglist.get(i).getName();
    }

    public static boolean hasImg() {
        return !imglist.isEmpty();
    }

    public static void updateFileList() {
        imglist.clear();
        try (Stream<Path> list = Files.list(imgFolder.toPath())) {
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
}
