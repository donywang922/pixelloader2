package com.ywsuoyi.pixelloader.colorspace;

import net.minecraft.world.level.block.Block;

import java.util.Objects;

public class SelectBlock {
    public ColorRGB bc, map, mapB, mapT;
    public Block block;

    public SelectBlock(Block block, ColorRGB bc, ColorRGB map, ColorRGB mapB, ColorRGB mapT) {
        this.bc = bc;
        this.map = map;
        this.mapT = mapT;
        this.mapB = mapB;
        this.block = block;
    }
}
