package com.ywsuoyi.pixelloader.colorspace;

import net.minecraft.world.level.block.Block;

public class ColoredBlock {
    public int r, g, b, y = 0;

    public Block block;

    public ColoredBlock(int r, int g, int b, Block block) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.block = block;
    }

    public ColoredBlock(int rgb, Block block, int y) {
        this((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, block);
        this.y = y;
    }

    public float rgbSq2(ColorRGB rgb) {
        float x = (r - rgb.r) * 0.3f, y = (g - rgb.g) * 0.59f, z = (b - rgb.b) * 0.11f;
        return x * x + y * y + z * z;
    }


    public float disSq(ColorRGB value) {
        float x = r - value.r, y = g - value.g, z = b - value.b;
        return x * x + y * y + z * z;
    }

    public float rgbSq(ColorRGB value) {
        int rmean = (r + value.r) / 2;
        int dr = r - value.r;
        int dg = g - value.g;
        int db = b - value.b;
        return (((512 + rmean) * dr * dr) >> 8) + 4 * dg * dg + (((767 - rmean) * db * db) >> 8);
    }
}
