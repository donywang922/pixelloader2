package com.ywsuoyi;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class Selections {
    public static List<String> cutout = new ArrayList<>(3);
    public static List<String> dither = new ArrayList<>(2);
    public static List<String> whitelist = new ArrayList<>(2);

    static {
        cutout.add(Component.translatable("pixelLoader.setting.screen.cutout.0").getString());
        cutout.add(Component.translatable("pixelLoader.setting.screen.cutout.1").getString());
        cutout.add(Component.translatable("pixelLoader.setting.screen.cutout.2").getString());

        dither.add(Component.translatable("pixelLoader.setting.screen.dither.0").getString());
        dither.add(Component.translatable("pixelLoader.setting.screen.dither.1").getString());

        whitelist.add(Component.translatable("pixelLoader.colorspace.screen.whitelist.0").getString());
        whitelist.add(Component.translatable("pixelLoader.colorspace.screen.whitelist.1").getString());
    }
}
