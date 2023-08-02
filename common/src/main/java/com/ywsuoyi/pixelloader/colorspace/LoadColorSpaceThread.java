package com.ywsuoyi.pixelloader.colorspace;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ywsuoyi.pixelloader.BaseThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
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
import java.util.*;

public class LoadColorSpaceThread extends BaseThread {
    public Level world;

    public LoadColorSpaceThread(Player player, Level world) {
        super(player);
        this.world = world;
    }

    @Override
    public void run() {
        ColorSpace.clearAll();
        Map<Block, String> colorBlocks = new HashMap<>();
        Set<Integer> colSet = new HashSet<>();
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        setMessage(Component.translatable("pixelLoader.colorspace.findblock"));
        for (Block b : Registry.BLOCK) {
            if (state == State.end) {
                onend(true);
                return;
            }
            BlockState blockState = b.defaultBlockState();
            boolean bool = Block.isShapeFullBlock(blockState.getShape(world, BlockPos.ZERO));
            bool &= !(b instanceof EntityBlock);
            for (ItemStack itemStack : ColorSpace.filter) {
                if (itemStack.getItem() == b.asItem()) {
                    bool = false;
                    break;
                }
            }
            if (bool) {
                MaterialColor color = blockState.getMapColor(world, BlockPos.ZERO);
                if (color.col != 0) {
                    int o = color.calculateRGBColor(MaterialColor.Brightness.LOW);
                    if (!colSet.contains(o)) {
                        colSet.add(o);
                        ColorSpace.mapSpace.addBlock(new ColoredBlock(o, b, -1));
                    }
                    o = color.calculateRGBColor(MaterialColor.Brightness.NORMAL);
                    if (!colSet.contains(o)) {
                        colSet.add(o);
                        ColorSpace.mapSpace.addBlock(new ColoredBlock(o, b, 0));
                        ColorSpace.mapFlatSpace.addBlock(new ColoredBlock(o, b, 0));
                    }
                    o = color.calculateRGBColor(MaterialColor.Brightness.HIGH);
                    if (!colSet.contains(o)) {
                        colSet.add(o);
                        ColorSpace.mapSpace.addBlock(new ColoredBlock(o, b, 1));
                    }
                }

                if (ItemBlockRenderTypes.getChunkRenderType(blockState) == RenderType.solid()) {
                    //block render
                    ResourceLocation id = Registry.BLOCK.getKey(b);
                    ResourceLocation id2 = new ResourceLocation(id.getNamespace(), "blockstates/" + id.getPath() + ".json");
                    try {
                        Optional<Resource> Resources = resourceManager.getResource(id2);
                        if (Resources.isPresent()) {
                            Resource r = Resources.get();
                            JsonObject json = JsonParser.parseReader(new InputStreamReader(r.open())).getAsJsonObject();
                            if (json.has("variants")) {
                                String s2 = "";
                                boolean b2 = true;
                                for (Map.Entry<String, JsonElement> variants : json.getAsJsonObject("variants").entrySet()) {
                                    String s = variants.getValue().isJsonArray() ? variants.getValue().getAsJsonArray().get(0).getAsJsonObject().get("model").getAsString() : variants.getValue().getAsJsonObject().get("model").getAsString();
                                    if (s2.isEmpty()) s2 = s;
                                    else b2 &= s.equals(s2);
                                }
                                if (b2) {
                                    //state
                                    String[] s3 = decompose(s2);
                                    ResourceLocation id3 = new ResourceLocation(s3[0], "models/" + s3[1] + ".json");
                                    Optional<Resource> Resources1 = resourceManager.getResource(id3);
                                    if (Resources1.isPresent()) {
                                        Resource r1 = Resources1.get();
                                        JsonObject json1 = JsonParser.parseReader(new InputStreamReader(r1.open())).getAsJsonObject();
                                        String s1 = "";
                                        boolean b1 = true;
                                        if (json1.has("textures")) {
                                            for (Map.Entry<String, JsonElement> j : json1.getAsJsonObject("textures").entrySet()) {
                                                if (s1.isEmpty()) s1 = j.getValue().getAsString();
                                                else b1 &= j.getValue().getAsString().equals(s1);
                                            }
                                            if (b1) {
//                                  //texture
                                                colorBlocks.put(b, s1);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        setMessage(Component.translatable("pixelLoader.colorspace.getcolor"));
        for (Map.Entry<Block, String> entry : colorBlocks.entrySet()) {
            if (state == State.end) {
                onend(true);
                return;
            }
            try {
                String[] s1 = decompose(entry.getValue());
                Optional<Resource> Resources = resourceManager.getResource(new ResourceLocation(s1[0], "textures/" + s1[1] + ".png"));
                Optional<Resource> TResources = resourceManager.getResource(new ResourceLocation(s1[0], "textures/" + s1[1] + ".png.mcmeta"));
                if (TResources.isEmpty() && Resources.isPresent()) {
                    Resource resource = Resources.get();
                    BufferedImage read = ImageIO.read(resource.open());
                    int width = read.getWidth();
                    int height = read.getHeight();
                    long sumR = 0, sumG = 0, sumB = 0;
                    boolean noA = true;
                    for (int y = read.getMinY(); y < height; y++) {
                        for (int x = read.getMinX(); x < width; x++) {
                            Color pixel = new Color(read.getRGB(x, y), true);
                            sumR += pixel.getRed();
                            sumG += pixel.getGreen();
                            sumB += pixel.getBlue();
                            noA &= pixel.getAlpha() == 255;
                        }
                    }
                    if (noA) {
                        int num = width * height;
                        ColorSpace.blockSpace.addBlock(new ColoredBlock((int) sumR / num, (int) sumG / num, (int) sumB / num, entry.getKey()));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
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
