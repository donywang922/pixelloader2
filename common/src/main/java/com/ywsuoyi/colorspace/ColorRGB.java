package com.ywsuoyi.colorspace;

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

    public static float rgbSq(ColorRGB rgb1, ColorRGB rgb2) {
        int rmean = (rgb1.r + rgb2.r) / 2;
        int dr = rgb1.r - rgb2.r;
        int dg = rgb1.g - rgb2.g;
        int db = rgb1.b - rgb2.b;
        return (((512 + rmean) * dr * dr) >> 8) + 4 * dg * dg + (((767 - rmean) * db * db) >> 8);
    }
}
