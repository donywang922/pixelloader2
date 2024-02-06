package com.ywsuoyi.pixelloader.colorspace;

public class ArrayColorSpace extends AbstractColorSpace {
    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void build() {
        load = true;
    }

    @Override
    public ColoredBlock getBlock(ColorRGB rgb) {
        if (rgb == null) return air;
        if (history[rgb.rgb] != null)
            return history[rgb.rgb];
        ColoredBlock block = air;
        float d = Float.MAX_VALUE;
        for (ColoredBlock coloredBlock : blocks) {
            float t = coloredBlock.rgbSq(rgb);
            if (t < d) {
                d = t;
                block = coloredBlock;
            }
        }
        history[rgb.rgb] = block;
        return block;
    }
}
