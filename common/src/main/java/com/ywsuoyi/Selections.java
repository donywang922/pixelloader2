package com.ywsuoyi;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class Selections {
    public static List<String> cutout = new ArrayList<>(3);
    public static List<String> dither = new ArrayList<>(2);
    public static List<String> support = new ArrayList<>(3);
    public static List<String> cover = new ArrayList<>(2);
    public static List<String> whitelist = new ArrayList<>(2);
    public static List<String> preset = new ArrayList<>(2);
    public static List<String> onfinish = new ArrayList<>(4);
    public static List<String> projectorMode = new ArrayList<>(2);
    public static List<List<String>> presets = new ArrayList<>(3);

    static {
        cutout.add(Component.translatable("pixelLoader.setting.screen.cutout.0").getString());
        cutout.add(Component.translatable("pixelLoader.setting.screen.cutout.1").getString());
        cutout.add(Component.translatable("pixelLoader.setting.screen.cutout.2").getString());

        dither.add(Component.translatable("pixelLoader.setting.screen.dither.0").getString());
        dither.add(Component.translatable("pixelLoader.setting.screen.dither.1").getString());

        whitelist.add(Component.translatable("pixelLoader.colorspace.screen.whitelist.0").getString());
        whitelist.add(Component.translatable("pixelLoader.colorspace.screen.whitelist.1").getString());

        support.add(Component.translatable("pixelLoader.setting.screen.support.0").getString());
        support.add(Component.translatable("pixelLoader.setting.screen.support.1").getString());
        support.add(Component.translatable("pixelLoader.setting.screen.support.2").getString());

        cover.add(Component.translatable("pixelLoader.setting.screen.cover.0").getString());
        cover.add(Component.translatable("pixelLoader.setting.screen.cover.1").getString());

        preset.add(Component.translatable("pixelLoader.setting.screen.pixel.preset.0").getString());
        preset.add(Component.translatable("pixelLoader.setting.screen.pixel.preset.1").getString());
        preset.add(Component.translatable("pixelLoader.setting.screen.pixel.preset.2").getString());

        onfinish.add(Component.translatable("pixelLoader.setting.screen.onfinish.0").getString());
        onfinish.add(Component.translatable("pixelLoader.setting.screen.onfinish.1").getString());
        onfinish.add(Component.translatable("pixelLoader.setting.screen.onfinish.2").getString());
        onfinish.add(Component.translatable("pixelLoader.setting.screen.onfinish.3").getString());

        projectorMode.add(Component.translatable("pixelLoader.projector.screen.mode.0").getString());
        projectorMode.add(Component.translatable("pixelLoader.projector.screen.mode.1").getString());

        ArrayList<String> vertical = new ArrayList<>();
        vertical.add("u+1");
        vertical.add("h-v");
        vertical.add("(z-1)*2");
        presets.add(vertical);

        ArrayList<String> flat = new ArrayList<>();
        flat.add("u+1");
        flat.add("0");
        flat.add("v");
        presets.add(flat);

        ArrayList<String> diagonal = new ArrayList<>();
        diagonal.add("sqrt(u*u/2)");
        diagonal.add("h-v");
        diagonal.add("sqrt(u*u/2)");
        presets.add(diagonal);
    }
}
