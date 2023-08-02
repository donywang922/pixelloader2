package com.ywsuoyi.pixelloader.colorspace;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ColorSpace {
    public static final ColorSpace blockSpace = new ColorSpace();
    public static final ColorSpace mapSpace = new ColorSpace();
    public static final ColorSpace mapFlatSpace = new ColorSpace();
    public static final ColoredBlock air = new ColoredBlock(0, Blocks.AIR, 0);
    public static LoadColorSpaceThread thread;

    public static NonNullList<ItemStack> filter = NonNullList.withSize(54, ItemStack.EMPTY);
    public static Boolean blackList = true;

    public static boolean waitPlace = false;
    public static boolean openFilter = false;
    ColoredBlock[] history;
    ColorTree tree;
    public NonNullList<ColoredBlock> blocks = NonNullList.create();
    boolean load = false;


    public void clear() {
        load = false;
        tree = null;
        blocks.clear();
        history = new ColoredBlock[16777216];
    }

    public void addBlock(ColoredBlock block) {
        blocks.add(block);
    }

    public void build() {
        tree = new ColorTree(blocks);
        load = true;
    }

    public ColoredBlock getBlockTree(ColorRGB rgb) {
        if (rgb == null) return air;
        if (history[rgb.rgb] != null)
            return history[rgb.rgb];
        ColoredBlock block = tree.getBlock(rgb);
        history[rgb.rgb] = block;
        return block;
    }

    public ColoredBlock getBlock(ColorRGB rgb) {
        if (rgb == null) return air;
        if (history[rgb.rgb] != null)
            return history[rgb.rgb];
        ColoredBlock block = air;
        float d = Float.MAX_VALUE;
        for (ColoredBlock coloredBlock : blocks) {
            float t = coloredBlock.rgbSq(rgb);
            if (t < d) {
                d = t;
                block = coloredBlock;
            }
        }
        history[rgb.rgb] = block;
        return block;
    }

    public static void clearAll() {
        blockSpace.clear();
        mapSpace.clear();
        mapFlatSpace.clear();
    }

    public static void buildAll() {
        blockSpace.build();
        mapSpace.build();
        mapFlatSpace.build();
    }

    public static boolean allLoad() {
        return blockSpace.load & mapFlatSpace.load & mapSpace.load;
    }
}
