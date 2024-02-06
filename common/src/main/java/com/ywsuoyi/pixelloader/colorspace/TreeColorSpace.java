package com.ywsuoyi.pixelloader.colorspace;

public class TreeColorSpace extends AbstractColorSpace {
    ColorTree tree;

    @Override
    public void clear() {
        super.clear();
        tree = null;
    }

    @Override
    public void build() {
        tree = new ColorTree(blocks);
        load = true;
    }

    @Override
    public ColoredBlock getBlock(ColorRGB rgb) {
        if (rgb == null) return air;
        if (history[rgb.rgb] != null)
            return history[rgb.rgb];
        ColoredBlock block = tree.getBlock(rgb);
        history[rgb.rgb] = block;
        return block;
    }
}
