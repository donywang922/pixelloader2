package com.ywsuoyi.pixelloader.colorspace;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class ColorSpace {
    public static final ColorSpace blockSpace = new ColorSpace();
    public static final ColorSpace mapSpace = new ColorSpace();
    public static final ColorSpace mapFlatSpace = new ColorSpace();
    public static final ColoredBlock air = new ColoredBlock(0, Blocks.AIR, 0);
    public static Thread thread;

    public static NonNullList<ItemStack> filter = NonNullList.withSize(54, ItemStack.EMPTY);
    ColoredBlock[] history;
    ColorTree tree;
    NonNullList<ColoredBlock> blocks = NonNullList.create();
    boolean load = false;


    public void clear() {
        load = false;
        tree = null;
        history = new ColoredBlock[16777216];
    }
    public void addBlock(ColoredBlock block){
            blocks.add(block);
    }
    public void build(){
        tree = new ColorTree(blocks);
        load = true;
    }
    public ColoredBlock getBlock(int rgb) {
        if (rgb == 0) return air;
        if (history[rgb]!=null)
            return history[rgb];
        ColoredBlock block = tree.getBlock(rgb);
        history[rgb] = block;
        return block;
    }

    public static void clearAll() {
        blockSpace.clear();
        mapSpace.clear();
        mapFlatSpace.clear();
    }

    public static void buildAll(){
        blockSpace.build();
        mapSpace.build();
        mapFlatSpace.build();
    }

    public static boolean allLoad(){
        return blockSpace.load& mapFlatSpace.load& mapSpace.load;
    }
}
