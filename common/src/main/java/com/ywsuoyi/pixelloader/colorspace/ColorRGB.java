package com.ywsuoyi.pixelloader.colorspace;

public class ColorRGB {
    public int r;
    public int g;
    public int b;
    public int rgb;

    public ColorRGB(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        rgb = (r << 16) + (g << 8) + b;
    }
}
