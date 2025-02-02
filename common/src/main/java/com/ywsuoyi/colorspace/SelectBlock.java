package com.ywsuoyi.colorspace;

import net.minecraft.world.level.block.Block;

public class SelectBlock {
    public ColorRGB bc, map, mapB, mapT;
    public Block block;
    public boolean active = true;

    public SelectBlock(Block block, ColorRGB bc, ColorRGB map, ColorRGB mapB, ColorRGB mapT) {
        this.bc = bc;
        this.map = map;
        this.mapT = mapT;
        this.mapB = mapB;
        this.block = block;
    }
}
