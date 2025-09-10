package com.ywsuoyi.colorspace;

import net.minecraft.world.level.block.Block;

public class SelectBlock {
    public ColorRGB bc, map, mapB, mapT;
    public float difM, difMB, difMT, lightWeight;
    public Block block;
    public boolean active = true;

    public SelectBlock(Block block, ColorRGB bc, ColorRGB map, ColorRGB mapB, ColorRGB mapT) {
        this(block, bc, map, mapB, mapT, block.defaultBlockState().getLightEmission() * ColorSpaces.lightWeight);
    }

    public SelectBlock(Block block, ColorRGB bc, ColorRGB map, ColorRGB mapB, ColorRGB mapT, float lightWeight) {
        this.bc = bc;
        this.map = map;
        this.mapT = mapT;
        this.mapB = mapB;
        this.block = block;
        difM = ColorRGB.rgbSq(bc, map);
        difMB = ColorRGB.rgbSq(bc, mapB);
        difMT = ColorRGB.rgbSq(bc, mapT);
        this.lightWeight = lightWeight;
    }
}
