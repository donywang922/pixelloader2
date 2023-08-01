package com.ywsuoyi.pixelloader.colorspace;

import net.minecraft.world.level.block.Block;

public class ColoredBlock {
    public int r, g, b, y = 0;
    public float tx,ty,tz;
    public Block block;

    public ColoredBlock(int r, int g, int b, Block block) {
        this.r = r;
        this.g = g;
        this.b = b;
        tx = r*3.333f;
        ty = g*1.694f;
        tz = b*9.090f;
        this.block = block;
    }
    public ColoredBlock(int rgb, Block block) {
        this((rgb >> 16) & 0xff,(rgb >> 8) & 0xff,rgb & 0xff,block);
    }
    public ColoredBlock(int rgb, Block block, int y) {
        this((rgb >> 16) & 0xff,(rgb >> 8) & 0xff,rgb & 0xff,block);
        this.y = y;
    }
    public float rgbSq(ColoredBlock value) {
        float x = (r-value.r)*0.3f,y = (g-value.g)*0.59f,z = (b-value.b)*0.11f;
        return x*x+y*y+z*z;
    }
    public float disSq(ColoredBlock value) {
        float x = r-value.r,y = g-value.g,z = b-value.b;
        return x*x+y*y+z*z;
    }
}
