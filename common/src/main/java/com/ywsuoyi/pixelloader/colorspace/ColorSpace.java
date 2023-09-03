package com.ywsuoyi.pixelloader.colorspace;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;
import java.util.Set;

public class ColorSpace {
    public static final ColorSpace blockSpace = new ColorSpace();
    public static final ColorSpace mapSpace = new ColorSpace();
    public static final ColorSpace mapFlatSpace = new ColorSpace();
    public static final ColoredBlock air = new ColoredBlock(0, Blocks.AIR, 0);
    public static LoadColorSpaceThread thread;

    public static NonNullList<ItemStack> filter = NonNullList.withSize(54, ItemStack.EMPTY);
    public static NonNullList<SelectBlock> selectBlocks = NonNullList.create();
    public static boolean whiteList = false;

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
        selectBlocks.clear();
        blockSpace.clear();
        mapSpace.clear();
        mapFlatSpace.clear();
    }

    public static void buildAll() {
        Set<Integer> colSet = new HashSet<>();
        for (SelectBlock block : selectBlocks) {
            if (!block.active) continue;
            blockSpace.blocks.add(new ColoredBlock(block.bc.rgb, block.block, 0));
            if (!colSet.contains(block.map.rgb)) {
                mapSpace.blocks.add(new ColoredBlock(block.map.rgb, block.block, 0));
                mapFlatSpace.blocks.add(new ColoredBlock(block.map.rgb, block.block, 0));
                colSet.add(block.map.rgb);
            }
            if (!colSet.contains(block.mapB.rgb)) {
                mapSpace.blocks.add(new ColoredBlock(block.mapB.rgb, block.block, -1));
                colSet.add(block.mapB.rgb);
            }
            if (!colSet.contains(block.mapT.rgb)) {
                mapSpace.blocks.add(new ColoredBlock(block.mapT.rgb, block.block, 1));
                colSet.add(block.mapT.rgb);
            }
        }
        blockSpace.build();
        mapSpace.build();
        mapFlatSpace.build();
    }

    public static void reBuildAll() {
        blockSpace.clear();
        mapSpace.clear();
        mapFlatSpace.clear();
        buildAll();
    }

    public static boolean allLoad() {
        return blockSpace.load & mapFlatSpace.load & mapSpace.load;
    }

}
