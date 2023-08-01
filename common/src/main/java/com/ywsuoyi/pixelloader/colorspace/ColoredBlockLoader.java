package com.ywsuoyi.pixelloader.colorspace;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ywsuoyi.pixelloader.Setting;
import com.ywsuoyi.pixelloader.banBlockInv;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.material.MaterialColor;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ColoredBlockLoader extends Item {

    public ColoredBlockLoader(Properties settings) {
        super(settings);
    }

    public void load(Level world, BlockPos pos, Player player) {
        ColorSpace.clearAll();
        Map<Block, String> blockStringMap = Maps.newHashMap();
//        Setting.colorBlockMap.clear();
//        Setting.mapColorBlockMap.clear();
//        Setting.mapltColorBlockMap.clear();
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        for (Block b : Registry.BLOCK) {
            boolean bool = Block.isShapeFullBlock(b.defaultBlockState().getShape(world, pos));
            bool &= !(b instanceof EntityBlock);
            for (ItemStack itemStack : Setting.banItem) {
                if (itemStack.getItem() == b.asItem()) {
                    bool = false;
                    break;
                }
            }
            if (bool) {
                if (b.defaultBlockState().getMapColor(world, BlockPos.ZERO).col != 0) {
                    ColorSpace.mapSpace.addBlock(new ColoredBlock(b.defaultBlockState().getMapColor(world, BlockPos.ZERO).calculateRGBColor(MaterialColor.Brightness.LOW), b, -1));
                    ColorSpace.mapSpace.addBlock(new ColoredBlock(b.defaultBlockState().getMapColor(world, BlockPos.ZERO).calculateRGBColor(MaterialColor.Brightness.NORMAL), b, 0));
                    ColorSpace.mapFlatSpace.addBlock(new ColoredBlock(b.defaultBlockState().getMapColor(world, BlockPos.ZERO).calculateRGBColor(MaterialColor.Brightness.NORMAL), b, 0));
                    ColorSpace.mapSpace.addBlock(new ColoredBlock(b.defaultBlockState().getMapColor(world, BlockPos.ZERO).calculateRGBColor(MaterialColor.Brightness.HIGH), b, 1));
//                    Setting.mapBlocks.add(new ColoredBlock(b.defaultBlockState().getMapColor(world, BlockPos.ZERO).calculateRGBColor(MaterialColor.Brightness.LOW), b, -1));
//                    Setting.mapBlocks.add(new ColoredBlock(b.defaultBlockState().getMapColor(world, BlockPos.ZERO).calculateRGBColor(MaterialColor.Brightness.NORMAL), b, 0));
//                    Setting.mapBlocks.add(new ColoredBlock(b.defaultBlockState().getMapColor(world, BlockPos.ZERO).calculateRGBColor(MaterialColor.Brightness.HIGH), b, 1));
                }

                if (ItemBlockRenderTypes.getChunkRenderType(b.defaultBlockState()) == RenderType.solid()) {
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
                                    if (s2.equals("")) s2 = s;
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
                                                if (s1.equals("")) s1 = j.getValue().getAsString();
                                                else b1 &= j.getValue().getAsString().equals(s1);
                                            }
                                            if (b1) {
//                                  //texture
                                                blockStringMap.put(b, s1);
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
//        Setting.coloredBlocks.clear();
        blockStringMap.forEach((block, s) -> {
            try {
                String[] s1 = decompose(s);
                Optional<Resource> Resources = resourceManager.getResource(new ResourceLocation(s1[0], "textures/" + s1[1] + ".png"));
                Optional<Resource> TResources = resourceManager.getResource(new ResourceLocation(s1[0], "textures/" + s1[1] + ".png.mcmeta"));
                if (TResources.isEmpty() && Resources.isPresent()) {
                    Resource resource = Resources.get();
                    BufferedImage read = ImageIO.read(resource.open());
                    int width = read.getWidth();
                    int height = read.getHeight();
                    long sumr = 0, sumg = 0, sumb = 0;
                    boolean noA = true;
                    for (int y = read.getMinY(); y < height; y++) {
                        for (int x = read.getMinX(); x < width; x++) {
                            Color pixel = new Color(read.getRGB(x, y), true);
                            sumr += pixel.getRed();
                            sumg += pixel.getGreen();
                            sumb += pixel.getBlue();
                            noA &= pixel.getAlpha() == 255;
                        }
                    }
                    if (noA) {
                        int num = width * height;
//                        Setting.coloredBlocks.add(new ColoredBlock((int) sumr / num, (int) sumg / num, (int) sumb / num, block));
                        ColorSpace.blockSpace.addBlock(new ColoredBlock((int) sumr / num, (int) sumg / num, (int) sumb / num, block));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        if (player != null)
            player.displayClientMessage(Component.translatable("pixelLoader.colored_block.loaded"), true);
        Setting.ed = true;
    }


    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand handIn) {
        if (!worldIn.isClientSide) if (player.isShiftKeyDown()) {
            load(worldIn, player.getOnPos(), player);
        } else {
            player.openMenu(new SimpleMenuProvider((id, inventory, playerIn) -> ChestMenu.sixRows(id, inventory, new banBlockInv()), Setting.banItemScreen));
        }
        return InteractionResultHolder.success(player.getItemInHand(handIn));
    }


    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            if (context.getPlayer() != null) if (context.getPlayer().isShiftKeyDown()) {
                if (Setting.ed) {
                    Setting.coloredBlocks.forEach(coloredBlock -> {
                        context.getLevel().setBlock(context.getClickedPos().offset(coloredBlock.r / 6, coloredBlock.g / 6 - 1, coloredBlock.b / 6), Blocks.GLASS.defaultBlockState(), 3);
                        context.getLevel().setBlock(context.getClickedPos().offset(coloredBlock.r / 6, coloredBlock.g / 6, coloredBlock.b / 6), coloredBlock.block.defaultBlockState(), 3);
                    });
                } else load(context.getLevel(), context.getClickedPos(), context.getPlayer());
            } else {
                context.getPlayer().openMenu(new SimpleMenuProvider((id, inventory, playerIn) -> ChestMenu.sixRows(id, inventory, new banBlockInv()), Setting.banItemScreen));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("pixelLoader.colored_block.tip"));
        if (Setting.ed) {
            tooltip.add(Component.translatable("pixelLoader.colored_block.place"));
            tooltip.add(Component.translatable("pixelLoader.colored_block.reload"));
        } else {
            tooltip.add(Component.translatable("pixelLoader.colored_block.load"));
        }
        super.appendHoverText(stack, world, tooltip, context);
    }

    public static String[] decompose(String resourceName) {
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
