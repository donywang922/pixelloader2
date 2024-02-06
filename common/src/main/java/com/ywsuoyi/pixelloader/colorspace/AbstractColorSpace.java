package com.ywsuoyi.pixelloader.colorspace;

import net.minecraft.core.NonNullList;
import net.minecraft.world.level.block.Blocks;

public abstract class AbstractColorSpace {
    public static final ColoredBlock air = new ColoredBlock(0, Blocks.AIR, 0);
    public static LoadColorSpaceThread thread;
    ColoredBlock[] history = new ColoredBlock[16777216];
    public NonNullList<ColoredBlock> blocks = NonNullList.create();
    boolean load = false;


    public void clear() {
        load = false;
        blocks.clear();
        history = new ColoredBlock[16777216];
    }

    public abstract void build();

    public abstract ColoredBlock getBlock(ColorRGB rgb);
}