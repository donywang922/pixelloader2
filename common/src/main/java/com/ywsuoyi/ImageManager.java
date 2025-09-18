package com.ywsuoyi;

import net.minecraft.network.chat.Component;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ImageManager {
    public static File imgFolder = new File("./img");
    public static final List<File> imglist = new ArrayList<>();

    public static List<String> getImageListStr() {
        updateFileList();
        List<String> suggestions = new java.util.ArrayList<>(imglist.stream().map(File::getName).toList());
        if (suggestions.isEmpty()) {
            suggestions.add(Component.translatable("pixelLoader.fileNotFind").getString());
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

        // 获取ImageIO实际支持的格式（动态检测）
        Set<String> actualSupportedFormats = Set.of(ImageIO.getReaderFormatNames());

        try (Stream<Path> list = Files.list(imgFolder.toPath())) {
            list.forEach(path -> {
                if (Files.isDirectory(path)) {
                    // 如果是文件夹，检查里面是否有图片文件
                    if (hasImagesInDirectory(path.toFile(), actualSupportedFormats)) {
                        imglist.add(path.toFile());
                    }
                } else {
                    // 如果是文件，检查是否为支持的图片格式
                    if (isImageFile(path.toFile(), actualSupportedFormats)) {
                        imglist.add(path.toFile());
                    }
                }
            });
        } catch (IOException e) {
            PixelLoader.logger.error("Failed to update file list: {}", e.getMessage());
        }

        // 按名称排序
        imglist.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
    }

    /**
     * 检查目录中是否包含图片文件
     */
    private static boolean hasImagesInDirectory(File directory, Set<String> supportedFormats) {
        try (Stream<Path> dirStream = Files.list(directory.toPath())) {
            return dirStream.filter(path -> !Files.isDirectory(path))
                    .anyMatch(path -> isImageFile(path.toFile(), supportedFormats));
        } catch (IOException e) {
            PixelLoader.logger.warn("Failed to scan directory {}: {}", directory.getName(), e.getMessage());
            return false;
        }
    }

    /**
     * 检查文件是否为支持的图片格式
     */
    public static boolean isImageFile(File file, Set<String> supportedFormats) {
        String fileName = file.getName().toLowerCase();
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return false; // 没有扩展名或扩展名为空
        }

        String extension = fileName.substring(dotIndex + 1);
        // 然后检查ImageIO实际支持的格式
        return supportedFormats.contains(extension.toUpperCase()) ||
                supportedFormats.contains(extension.toLowerCase()) ||
                supportedFormats.contains(extension);
    }

    /**
     * 获取所有支持的图片格式（用于调试）
     */
    public static String[] getAllSupportedFormats() {
        return ImageIO.getReaderFormatNames();
    }

    /**
     * 打印所有支持的格式到日志（用于调试）
     */
    public static void logSupportedFormats() {
        String[] formats = ImageIO.getReaderFormatNames();
        PixelLoader.logger.info("ImageIO supported formats: {}", Arrays.toString(formats));

        String[] mimeTypes = ImageIO.getReaderMIMETypes();
        PixelLoader.logger.info("ImageIO supported MIME types: {}", Arrays.toString(mimeTypes));
    }

    /**
     * 从文件夹中获取第一张图片文件
     */
    public static File getFirstImageFromDirectory(File directory) {
        if (!directory.isDirectory()) return null;

        // 获取ImageIO实际支持的格式
        Set<String> actualSupportedFormats = Set.of(ImageIO.getReaderFormatNames());

        try (Stream<Path> dirStream = Files.list(directory.toPath())) {
            return dirStream.filter(path -> !Files.isDirectory(path))
                    .filter(path -> isImageFile(path.toFile(), actualSupportedFormats))
                    .map(Path::toFile)
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            return null;
        }
    }
    public static List<File> getAllImagesFromDirectory(File directory) {
        List<File> imageFiles = new ArrayList<>();
        if (!directory.isDirectory()) return imageFiles;

        Set<String> actualSupportedFormats = Set.of(ImageIO.getReaderFormatNames());

        try (Stream<Path> dirStream = Files.list(directory.toPath())) {
            dirStream.filter(path -> !Files.isDirectory(path))
                    .filter(path -> isImageFile(path.toFile(), actualSupportedFormats))
                    .map(Path::toFile)
                    .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                    .forEach(imageFiles::add);
        } catch (IOException e) {
            PixelLoader.logger.warn("Failed to scan directory {}: {}", directory.getName(), e.getMessage());
        }

        return imageFiles;
    }
}