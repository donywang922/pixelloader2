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

    public ColorRGB(int rgb) {
        r = (rgb >> 16) & 0xff;
        g = (rgb >> 8) & 0xff;
        b = rgb & 0xff;
        this.rgb = rgb;
    }

    public static ColorRGB BGR(int bgr) {
        int b = (bgr >> 16) & 0xff;
        int g = (bgr >> 8) & 0xff;
        int r = bgr & 0xff;
        return new ColorRGB(r, g, b);
    }
}
