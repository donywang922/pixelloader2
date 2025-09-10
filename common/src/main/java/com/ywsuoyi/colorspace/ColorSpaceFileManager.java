package com.ywsuoyi.colorspace;

import com.google.gson.*;
import com.ywsuoyi.PixelLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ColorSpaceFileManager {
    private static final String COLORS_FOLDER = "colors";
    private static final String FILE_EXTENSION = ".json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 获取colors文件夹路径
     */
    private static Path getColorsFolder() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve(COLORS_FOLDER);
    }

    /**
     * 确保colors文件夹存在
     */
    private static void ensureColorsFolderExists() {
        try {
            Path colorsPath = getColorsFolder();
            if (!Files.exists(colorsPath)) {
                Files.createDirectories(colorsPath);
            }
        } catch (IOException e) {
            PixelLoader.logger.error("Failed to create colors directory: {}", e.getMessage());
        }
    }

    /**
     * 保存色域文件
     */
    public static boolean saveColorSpace(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            PixelLoader.logger.error("Color space filename cannot be empty");
            return false;
        }

        ensureColorsFolderExists();

        try {
            // 构建保存数据
            JsonObject root = new JsonObject();

            // 保存过滤器模式
            root.addProperty("whiteList", ColorSpaces.whiteList);
            root.addProperty("lightWeight", ColorSpaces.lightWeight);

            // 保存过滤器物品
            JsonArray filterArray = new JsonArray();
            for (ItemStack stack : ColorSpaces.filter) {
                if (!stack.isEmpty()) {
                    JsonObject itemData = serializeItemStack(stack);
                    if (itemData != null) {
                        filterArray.add(itemData);
                    }
                }
            }
            root.add("filter", filterArray);

            // 保存选择的方块列表
            JsonArray selectBlocksArray = new JsonArray();
            for (SelectBlock selectBlock : ColorSpaces.selectBlocks) {
                JsonObject blockData = new JsonObject();

                // 方块ID
                ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(selectBlock.block);
                blockData.addProperty("block", blockId.toString());

                // 是否启用
                blockData.addProperty("active", selectBlock.active);

                // 四种颜色
                blockData.add("bc", serializeColorRGB(selectBlock.bc));
                blockData.addProperty("lightWeight", selectBlock.lightWeight);
                blockData.add("map", serializeColorRGB(selectBlock.map));
                blockData.add("mapB", serializeColorRGB(selectBlock.mapB));
                blockData.add("mapT", serializeColorRGB(selectBlock.mapT));

                selectBlocksArray.add(blockData);
            }
            root.add("selectBlocks", selectBlocksArray);

            // 写入文件
            String finalFileName = fileName.endsWith(FILE_EXTENSION) ? fileName : fileName + FILE_EXTENSION;
            Path filePath = getColorsFolder().resolve(finalFileName);

            try (FileWriter writer = new FileWriter(filePath.toFile(), StandardCharsets.UTF_8)) {
                GSON.toJson(root, writer);
            }

            PixelLoader.logger.info("Color space saved successfully: {}", finalFileName);
            return true;

        } catch (Exception e) {
            PixelLoader.logger.error("Failed to save color space: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 读取色域文件
     */
    public static boolean loadColorSpace(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            PixelLoader.logger.error("Color space filename cannot be empty");
            return false;
        }

        try {
            String finalFileName = fileName.endsWith(FILE_EXTENSION) ? fileName : fileName + FILE_EXTENSION;
            Path filePath = getColorsFolder().resolve(finalFileName);

            if (!Files.exists(filePath)) {
                PixelLoader.logger.error("Color space file not found: {}", finalFileName);
                return false;
            }

            // 读取文件
            String jsonContent = Files.readString(filePath, StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(jsonContent).getAsJsonObject();

            // 清除现有数据
            ColorSpaces.clearAll();
            ColorSpaces.filter.clear();

            // 读取过滤器模式
            if (root.has("whiteList")) {
                ColorSpaces.whiteList = root.get("whiteList").getAsInt();
            }

            if (root.has("lightWeight")) {
                ColorSpaces.lightWeight = root.get("lightWeight").getAsFloat();
            }

            // 读取过滤器物品
            if (root.has("filter")) {
                JsonArray filterArray = root.getAsJsonArray("filter");
                int index = 0;
                for (JsonElement element : filterArray) {
                    if (index >= 54) break; // 防止超出范围

                    ItemStack stack = deserializeItemStack(element.getAsJsonObject());
                    if (stack != null && !stack.isEmpty()) {
                        ColorSpaces.filter.set(index, stack);
                    }
                    index++;
                }
            }

            // 读取选择的方块列表
            if (root.has("selectBlocks")) {
                JsonArray selectBlocksArray = root.getAsJsonArray("selectBlocks");
                for (JsonElement element : selectBlocksArray) {
                    JsonObject blockData = element.getAsJsonObject();

                    try {
                        // 解析方块
                        String blockIdStr = blockData.get("block").getAsString();
                        ResourceLocation blockId = ResourceLocation.parse(blockIdStr);
                        Block block = BuiltInRegistries.BLOCK.get(blockId);

                        // 解析颜色
                        ColorRGB bc = deserializeColorRGB(blockData.getAsJsonObject("bc"));
                        ColorRGB map = deserializeColorRGB(blockData.getAsJsonObject("map"));
                        ColorRGB mapB = deserializeColorRGB(blockData.getAsJsonObject("mapB"));
                        ColorRGB mapT = deserializeColorRGB(blockData.getAsJsonObject("mapT"));
                        float lightWeight = blockData.has("lightWeight") ? blockData.get("lightWeight").getAsFloat() : 0;

                        // 创建SelectBlock
                        SelectBlock selectBlock = new SelectBlock(block, bc, map, mapB, mapT, lightWeight);

                        // 设置是否启用
                        if (blockData.has("active")) {
                            selectBlock.active = blockData.get("active").getAsBoolean();
                        }

                        ColorSpaces.selectBlocks.add(selectBlock);

                    } catch (Exception e) {
                        PixelLoader.logger.warn("Failed to deserialize block data: {}", e.getMessage());
                    }
                }
            }

            PixelLoader.logger.info("Color space loaded successfully: {}", finalFileName);
            return true;

        } catch (Exception e) {
            PixelLoader.logger.error("Failed to load color space: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取colors文件夹中所有文件名（不含扩展名）
     */
    public static List<String> getAvailableColorSpaceFiles() {
        List<String> fileNames = new ArrayList<>();
        Path colorsPath = getColorsFolder();
        if (!Files.exists(colorsPath)) {
            return fileNames;
        }
        try (Stream<Path> stream = Files.list(getColorsFolder())) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().toLowerCase().endsWith(FILE_EXTENSION))
                    .forEach(path -> {
                        String fileName = path.getFileName().toString();
                        // 移除扩展名
                        String nameWithoutExt = fileName.substring(0, fileName.length() - FILE_EXTENSION.length());
                        fileNames.add(nameWithoutExt);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileNames;
    }

    /**
     * 序列化ItemStack
     */
    private static JsonObject serializeItemStack(ItemStack stack) {
        try {
            JsonObject itemData = new JsonObject();

            // 物品ID
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            itemData.addProperty("item", itemId.toString());
            // 处理命名牌的自定义名称
            if (stack.getItem() == Items.NAME_TAG) {
                Component customName = stack.get(DataComponents.CUSTOM_NAME);
                if (customName != null) {
                    itemData.addProperty("customName", customName.getString());
                }
            }

            return itemData;
        } catch (Exception e) {
            PixelLoader.logger.warn("Failed to serialize ItemStack: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 反序列化ItemStack
     */
    private static ItemStack deserializeItemStack(JsonObject itemData) {
        try {
            String itemIdStr = itemData.get("item").getAsString();
            ResourceLocation itemId = ResourceLocation.parse(itemIdStr);
            ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(itemId), 1);

            // 处理命名牌的自定义名称
            if (stack.getItem() == Items.NAME_TAG && itemData.has("customName")) {
                String customName = itemData.get("customName").getAsString();
                stack.set(DataComponents.CUSTOM_NAME, Component.literal(customName));
            }

            return stack;
        } catch (Exception e) {
            PixelLoader.logger.warn("Failed to deserialize ItemStack: {}", e.getMessage());
            return ItemStack.EMPTY;
        }
    }

    /**
     * 序列化ColorRGB
     */
    private static JsonObject serializeColorRGB(ColorRGB color) {
        JsonObject colorData = new JsonObject();
        colorData.addProperty("r", color.r);
        colorData.addProperty("g", color.g);
        colorData.addProperty("b", color.b);
        return colorData;
    }

    /**
     * 反序列化ColorRGB
     */
    private static ColorRGB deserializeColorRGB(JsonObject colorData) {
        int r = colorData.get("r").getAsInt();
        int g = colorData.get("g").getAsInt();
        int b = colorData.get("b").getAsInt();
        return new ColorRGB(r, g, b);
    }
}