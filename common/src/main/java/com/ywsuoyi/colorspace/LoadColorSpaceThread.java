package com.ywsuoyi.colorspace;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ywsuoyi.PixelLoader;
import com.ywsuoyi.loadingThreadUtil.BaseThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadColorSpaceThread extends BaseThread {
    public Level world;
    private final HashSet<TagKey<Block>> tagFilter = new HashSet<>();
    private final HashSet<Block> blockFilter = new HashSet<>();
    private static final float MAX_ANIMATION_COLOR_DIFF = 15.0f; // 最大允许的帧间色差

    public LoadColorSpaceThread(Player player, Level world) {
        super(player);
        this.world = world;
    }

    @Override
    public void run() {
        setMessage(Component.translatable("pixelLoader.colorspace.checkfilter"));
        for (ItemStack stack : ColorSpaces.filter) {
            if (stack.getItem() instanceof BlockItem block) {
                blockFilter.add(block.getBlock());
            } else if (stack.getItem() == Items.NAME_TAG) {
                // 处理命名牌：使用命名牌的名字作为标签过滤器
                Component customName = stack.get(DataComponents.CUSTOM_NAME);
                if (customName != null) {
                    String tagName = customName.getString();
                    try {
                        // 尝试解析标签名称为 ResourceLocation
                        ResourceLocation tagLocation;
                        if (tagName.contains(":")) {
                            tagLocation = ResourceLocation.parse(tagName);
                        } else {
                            // 如果没有命名空间，默认使用 minecraft
                            tagLocation = ResourceLocation.fromNamespaceAndPath("minecraft", tagName);
                        }

                        // 创建标签键并添加到过滤器
                        TagKey<Block> tagKey = TagKey.create(BuiltInRegistries.BLOCK.key(), tagLocation);
                        tagFilter.add(tagKey);
                    } catch (Exception e) {
                        PixelLoader.logger.warn("Invalid tag name in name tag: {}", tagName);
                    }
                }
            }
        }
        ColorSpaces.clearAll();
        ArrayList<Tuple<Block, String>> colorBlocks = new ArrayList<>();
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        setMessage(Component.translatable("pixelLoader.colorspace.findblock"));
        for (Block b : BuiltInRegistries.BLOCK) {
            if (state == State.end) {
                onend(true);
                return;
            }

            if (b instanceof EntityBlock) continue;//not a block entity
            BlockState blockState = b.defaultBlockState();
            if (!Block.isShapeFullBlock(blockState.getShape(world, BlockPos.ZERO))) continue;//full block
            AtomicBoolean inFilter = new AtomicBoolean(false);
            blockState.getTags().forEach(blockTagKey -> {
                if (tagFilter.contains(blockTagKey)) inFilter.set(true);//in tag filter
            });
            if (blockFilter.contains(b)) inFilter.set(true);//in filter
            if (inFilter.get() != ColorSpaces.whiteList) continue;//in filter & white list | out filter & black list
            if (ItemBlockRenderTypes.getChunkRenderType(blockState) != RenderType.solid()) continue;//opaque texture
            //block render
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(b);
            ResourceLocation id2 = PixelLoader.loc(id.getNamespace(), "blockstates/" + id.getPath() + ".json");
            try {
                Optional<Resource> Resources = resourceManager.getResource(id2);
                if (Resources.isEmpty()) continue;//must have states
                Resource r = Resources.get();
                JsonObject json = JsonParser.parseReader(new InputStreamReader(r.open())).getAsJsonObject();
                if (!json.has("variants")) continue;//multipart not consider
                String s2 = "";
                boolean b2 = true;
                for (Map.Entry<String, JsonElement> variants : json.getAsJsonObject("variants").entrySet()) {
                    String s = variants.getValue().isJsonArray() ? variants.getValue().getAsJsonArray().get(0).getAsJsonObject().get("model").getAsString() : variants.getValue().getAsJsonObject().get("model").getAsString();
                    if (s2.isEmpty()) s2 = s;
                    else b2 &= s.equals(s2);
                }
                if (!b2) continue;//only has one model
                //state
                String[] s3 = decompose(s2);
                ResourceLocation id3 = PixelLoader.loc(s3[0], "models/" + s3[1] + ".json");
                Optional<Resource> Resources1 = resourceManager.getResource(id3);
                if (Resources1.isEmpty()) continue;//must have model
                Resource r1 = Resources1.get();
                JsonObject json1 = JsonParser.parseReader(new InputStreamReader(r1.open())).getAsJsonObject();
                String s1 = "";
                boolean b1 = true;
                if (!json1.has("textures")) continue;//must have texture
                for (Map.Entry<String, JsonElement> j : json1.getAsJsonObject("textures").entrySet()) {
                    if (s1.isEmpty()) s1 = j.getValue().getAsString();
                    else b1 &= j.getValue().getAsString().equals(s1);
                }
                if (b1) colorBlocks.add(new Tuple<>(b, s1)); //all 6 face have same texture
            } catch (IOException e) {
                PixelLoader.logger.error("Failed to generate colorspace: {}", e.getMessage());
            }
        }
        setMessage(Component.translatable("pixelLoader.colorspace.getcolor"));
        L1:
        for (Tuple<Block, String> entry : colorBlocks) {
            if (state == State.end) {
                onend(true);
                return;
            }
            try {
                String[] s1 = decompose(entry.getB());
                Optional<Resource> Resources = resourceManager.getResource(PixelLoader.loc(s1[0], "textures/" + s1[1] + ".png"));
                Optional<Resource> TResources = resourceManager.getResource(PixelLoader.loc(s1[0], "textures/" + s1[1] + ".png.mcmeta"));

                if (Resources.isPresent()) {
                    Resource resource = Resources.get();
                    BufferedImage image = ImageIO.read(resource.open());
                    int width = image.getWidth();
                    int height = image.getHeight();

                    // 检查是否有动画
                    boolean hasAnimation = TResources.isPresent();
                    ColorRGB averageColor = null;
                    boolean isValidTexture = false;

                    if (!hasAnimation) {
                        // 无动画，直接处理
                        ColorRGB frameColor = calculateFrameColor(image, width, height);
                        if (frameColor != null) {
                            averageColor = frameColor;
                            isValidTexture = true;
                        }
                    } else {
                        // 有动画，需要检查帧间色差
                        AnimationResult animResult = processAnimatedTexture(image, TResources.get(), width, height);
                        if (animResult != null && animResult.isValid) {
                            averageColor = animResult.averageColor;
                            isValidTexture = true;
                        }
                    }

                    if (isValidTexture && averageColor != null) {
                        MapColor color = entry.getA().defaultBlockState().getMapColor(world, BlockPos.ZERO);
                        ColorSpaces.selectBlocks.add(new SelectBlock(entry.getA(),
                                averageColor,
                                ColorRGB.BGR(color.calculateRGBColor(MapColor.Brightness.NORMAL)),
                                ColorRGB.BGR(color.calculateRGBColor(MapColor.Brightness.LOW)),
                                ColorRGB.BGR(color.calculateRGBColor(MapColor.Brightness.HIGH))));
                    }
                }
            } catch (IOException e) {
                PixelLoader.logger.error("Failed to load color space: {}", e.getMessage());
            }
        }
        setMessage(Component.translatable("pixelLoader.colorspace.map"));
        ColorSpaces.buildAll();
        onend(false);
        setMessage(Component.translatable("pixelLoader.colorspace.loaded"));
    }

    /**
     * 计算单帧图像的平均颜色
     */
    private ColorRGB calculateFrameColor(BufferedImage image, int width, int height) {
        long sumR = 0, sumG = 0, sumB = 0;
        int validPixels = 0;

        for (int y = image.getMinY(); y < height; y++) {
            for (int x = image.getMinX(); x < width; x++) {
                Color pixel = new Color(image.getRGB(x, y), true);
                if (pixel.getAlpha() != 255) {
                    // 纹理不完全不透明，跳过这个方块
                    return null;
                }
                sumR += pixel.getRed();
                sumG += pixel.getGreen();
                sumB += pixel.getBlue();
                validPixels++;
            }
        }

        if (validPixels == 0) return null;

        return new ColorRGB((int) (sumR / validPixels), (int) (sumG / validPixels), (int) (sumB / validPixels));
    }

    /**
     * 处理动画纹理
     */
    private AnimationResult processAnimatedTexture(BufferedImage image, Resource metaResource, int width, int height) {
        try {
            // 读取动画元数据
            JsonObject meta = JsonParser.parseReader(new InputStreamReader(metaResource.open())).getAsJsonObject();
            JsonObject animation = meta.has("animation") ? meta.getAsJsonObject("animation") : new JsonObject();

            // 计算帧数
            int frameHeight = animation.has("height") ? animation.get("height").getAsInt() : width;
            int frameCount = height / frameHeight;

            if (frameCount <= 1) {
                // 不是真正的动画，按静态纹理处理
                return new AnimationResult(calculateFrameColor(image, width, frameHeight), true);
            }

            // 计算每帧的平均颜色
            java.util.List<ColorRGB> frameColors = new ArrayList<>();
            for (int frame = 0; frame < frameCount; frame++) {
                BufferedImage frameImage = image.getSubimage(0, frame * frameHeight, width, frameHeight);
                ColorRGB frameColor = calculateFrameColor(frameImage, width, frameHeight);
                if (frameColor == null) {
                    // 某帧包含透明像素，跳过整个方块
                    return null;
                }
                frameColors.add(frameColor);
            }

            // 计算帧间色差
            float maxColorDiff = 0;
            for (int i = 0; i < frameColors.size(); i++) {
                for (int j = i + 1; j < frameColors.size(); j++) {
                    float diff = rgbSq2(frameColors.get(i), frameColors.get(j));
                    maxColorDiff = Math.max(maxColorDiff, diff);
                }
            }

            // 检查色差是否在允许范围内
            if (maxColorDiff <= MAX_ANIMATION_COLOR_DIFF * MAX_ANIMATION_COLOR_DIFF) {
                // 计算所有帧的平均颜色
                long totalR = 0, totalG = 0, totalB = 0;
                for (ColorRGB color : frameColors) {
                    totalR += color.r;
                    totalG += color.g;
                    totalB += color.b;
                }

                ColorRGB averageColor = new ColorRGB(
                        (int) (totalR / frameColors.size()),
                        (int) (totalG / frameColors.size()),
                        (int) (totalB / frameColors.size())
                );

                return new AnimationResult(averageColor, true);
            } else {
                // 帧间色差过大，拒绝这个方块
                return new AnimationResult(null, false);
            }

        } catch (IOException e) {
            PixelLoader.logger.error("Failed to process animated texture: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 计算加权RGB色差的平方
     */
    private float rgbSq2(ColorRGB color1, ColorRGB color2) {
        float x = (color1.r - color2.r) * 0.3f;
        float y = (color1.g - color2.g) * 0.59f;
        float z = (color1.b - color2.b) * 0.11f;
        return x * x + y * y + z * z;
    }

    public String[] decompose(String resourceName) {
        String[] astring = new String[]{"minecraft", resourceName};
        int i = resourceName.indexOf(':');
        if (i >= 0) {
            astring[1] = resourceName.substring(i + 1);
            if (i >= 1) {
                astring[0] = resourceName.substring(0, i);
            }
        }
        return astring;
    }

    /**
     * 动画处理结果
     */
    private static class AnimationResult {
        final ColorRGB averageColor;
        final boolean isValid;

        AnimationResult(ColorRGB averageColor, boolean isValid) {
            this.averageColor = averageColor;
            this.isValid = isValid;
        }
    }
}