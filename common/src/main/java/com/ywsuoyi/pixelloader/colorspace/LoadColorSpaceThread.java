package com.ywsuoyi.pixelloader.colorspace;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ywsuoyi.pixelloader.PixelLoader;
import com.ywsuoyi.pixelloader.loadingThreadUtil.BaseThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;

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

    public LoadColorSpaceThread(Player player, Level world) {
        super(player);
        this.world = world;
    }

    @Override
    public void run() {
        setMessage(Component.translatable("pixelLoader.colorspace.checkfilter"));
        for (ItemStack stack : ColorSpace.filter) {
            if (stack.getItem() instanceof BlockItem block) {
                if (stack.getCount() > 1) {
                    block.getBlock().defaultBlockState().getTags().forEach(blockTagKey -> {
                        System.out.println(blockTagKey.location().getPath());
                        if (!blockTagKey.location().getPath().startsWith("mineable")) tagFilter.add(blockTagKey);
                    });
                } else {
                    blockFilter.add(block.getBlock());
                }
            }
        }
        ColorSpace.clearAll();
        ArrayList<Tuple<Block, String>> colorBlocks = new ArrayList<>();
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        setMessage(Component.translatable("pixelLoader.colorspace.findblock"));
        for (Block b : Registry.BLOCK) {
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
            if (inFilter.get() != ColorSpace.whiteList) continue;//in filter & white list | out filter & black list
            if (ItemBlockRenderTypes.getChunkRenderType(blockState) != RenderType.solid()) continue;//opaque texture
            //block render
            ResourceLocation id = Registry.BLOCK.getKey(b);
            ResourceLocation id2 = new ResourceLocation(id.getNamespace(), "blockstates/" + id.getPath() + ".json");
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
                ResourceLocation id3 = new ResourceLocation(s3[0], "models/" + s3[1] + ".json");
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
                Optional<Resource> Resources = resourceManager.getResource(new ResourceLocation(s1[0], "textures/" + s1[1] + ".png"));
                Optional<Resource> TResources = resourceManager.getResource(new ResourceLocation(s1[0], "textures/" + s1[1] + ".png.mcmeta"));
                if (TResources.isEmpty() && Resources.isPresent()) {
                    Resource resource = Resources.get();
                    BufferedImage read = ImageIO.read(resource.open());
                    int width = read.getWidth();
                    int height = read.getHeight();
                    long sumR = 0, sumG = 0, sumB = 0;
                    for (int y = read.getMinY(); y < height; y++) {
                        for (int x = read.getMinX(); x < width; x++) {
                            Color pixel = new Color(read.getRGB(x, y), true);
                            if (pixel.getAlpha() != 255) continue L1;//texture not opaque
                            sumR += pixel.getRed();
                            sumG += pixel.getGreen();
                            sumB += pixel.getBlue();
                        }
                    }
                    int num = width * height;
                    MaterialColor color = entry.getA().defaultBlockState().getMapColor(world, BlockPos.ZERO);
                    ColorSpace.selectBlocks.add(new SelectBlock(entry.getA(),
                            new ColorRGB((int) sumR / num, (int) sumG / num, (int) sumB / num),
                            ColorRGB.BGR(color.calculateRGBColor(MaterialColor.Brightness.NORMAL)),
                            ColorRGB.BGR(color.calculateRGBColor(MaterialColor.Brightness.LOW)),
                            ColorRGB.BGR(color.calculateRGBColor(MaterialColor.Brightness.HIGH))));
                }
            } catch (IOException e) {
                PixelLoader.logger.error("Failed to load color space: {}", e.getMessage());
            }
        }
        setMessage(Component.translatable("pixelLoader.colorspace.map"));
        ColorSpace.buildAll();
        onend(false);
        setMessage(Component.translatable("pixelLoader.colorspace.loaded"));
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
}
