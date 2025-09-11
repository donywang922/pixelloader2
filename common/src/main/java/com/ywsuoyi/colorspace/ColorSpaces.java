package com.ywsuoyi.colorspace;

import net.minecraft.core.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ColorSpaces {
    public static final TreeColorSpace blockSpace = new TreeColorSpace();
    public static final TreeColorSpace mapSpace = new TreeColorSpace();
    public static final TreeColorSpace map0Space = new TreeColorSpace();
    public static final TreeColorSpace mapDownSpace = new TreeColorSpace();
    public static final TreeColorSpace mapUpSpace = new TreeColorSpace();
    public static final ArrayList<ColoredBlock> beaconSpace = new ArrayList<>();

    public static LoadColorSpaceThread thread;

    public static NonNullList<ItemStack> filter = NonNullList.withSize(54, ItemStack.EMPTY);
    public static NonNullList<SelectBlock> selectBlocks = NonNullList.create();
    public static int whiteList = 0;
    public static String fileName = "";
    public static float lightWeight = 0;

    public static boolean waitPlace = false;
    public static boolean openFilter = false;


    public static void clearAll() {
        selectBlocks.clear();
        blockSpace.clear();
        mapSpace.clear();
        map0Space.clear();
        mapUpSpace.clear();
        mapDownSpace.clear();
    }

    public static void buildAll() {
        Map<Integer, Tuple<Float, ColoredBlock>> mapColorTracker = new HashMap<>();
        Map<Integer, Tuple<Float, ColoredBlock>> map0ColorTracker = new HashMap<>();
        Map<Integer, Tuple<Float, ColoredBlock>> mapDownColorTracker = new HashMap<>();
        Map<Integer, Tuple<Float, ColoredBlock>> mapUpColorTracker = new HashMap<>();

        for (SelectBlock block : selectBlocks) {
            if (!block.active) continue;
            blockSpace.blocks.add(new ColoredBlock(block.bc.rgb, block.lightWeight, block.block, 0));

            addMBlock(block.map.rgb, block.difM, block, 0, mapColorTracker);
            addMBlock(block.mapB.rgb, block.difMB, block, -1, mapColorTracker);
            addMBlock(block.mapT.rgb, block.difMT, block, 1, mapColorTracker);

            addMBlock(block.map.rgb, block.difM, block, 0, map0ColorTracker);
            addMBlock(block.mapB.rgb, block.difMB, block, -1, mapDownColorTracker);
            addMBlock(block.mapT.rgb, block.difMT, block, 1, mapUpColorTracker);
        }

        mapColorTracker.forEach((rgb, tuple) -> mapSpace.blocks.add(tuple.getB()));
        map0ColorTracker.forEach((rgb, tuple) -> map0Space.blocks.add(tuple.getB()));
        mapDownColorTracker.forEach((rgb, tuple) -> mapDownSpace.blocks.add(tuple.getB()));
        mapUpColorTracker.forEach((rgb, tuple) -> mapUpSpace.blocks.add(tuple.getB()));

        blockSpace.build();
        mapSpace.build();
        map0Space.build();
        mapDownSpace.build();
        mapUpSpace.build();
    }

    static {
        beaconSpace.add(new ColoredBlock(DyeColor.WHITE.getTextureDiffuseColor(), 0, Blocks.WHITE_STAINED_GLASS_PANE, 0));
        beaconSpace.add(new ColoredBlock(DyeColor.ORANGE.getTextureDiffuseColor(), 0, Blocks.ORANGE_STAINED_GLASS_PANE, 0));
        beaconSpace.add(new ColoredBlock(DyeColor.MAGENTA.getTextureDiffuseColor(), 0, Blocks.MAGENTA_STAINED_GLASS_PANE, 0));
        beaconSpace.add(new ColoredBlock(DyeColor.LIGHT_BLUE.getTextureDiffuseColor(), 0, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, 0));
        beaconSpace.add(new ColoredBlock(DyeColor.YELLOW.getTextureDiffuseColor(), 0, Blocks.YELLOW_STAINED_GLASS_PANE, 0));
        beaconSpace.add(new ColoredBlock(DyeColor.LIME.getTextureDiffuseColor(), 0, Blocks.LIME_STAINED_GLASS_PANE, 0));
        beaconSpace.add(new ColoredBlock(DyeColor.PINK.getTextureDiffuseColor(), 0, Blocks.PINK_STAINED_GLASS_PANE, 0));
        beaconSpace.add(new ColoredBlock(DyeColor.GRAY.getTextureDiffuseColor(), 0, Blocks.GRAY_STAINED_GLASS_PANE, 0));
        beaconSpace.add(new ColoredBlock(DyeColor.LIGHT_GRAY.getTextureDiffuseColor(), 0, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, 0));
        beaconSpace.add(new ColoredBlock(DyeColor.CYAN.getTextureDiffuseColor(), 0, Blocks.CYAN_STAINED_GLASS_PANE, 0));
        beaconSpace.add(new ColoredBlock(DyeColor.PURPLE.getTextureDiffuseColor(), 0, Blocks.PURPLE_STAINED_GLASS_PANE, 0));
        beaconSpace.add(new ColoredBlock(DyeColor.BLUE.getTextureDiffuseColor(), 0, Blocks.BLUE_STAINED_GLASS_PANE, 0));
        beaconSpace.add(new ColoredBlock(DyeColor.BROWN.getTextureDiffuseColor(), 0, Blocks.BROWN_STAINED_GLASS_PANE, 0));
        beaconSpace.add(new ColoredBlock(DyeColor.GREEN.getTextureDiffuseColor(), 0, Blocks.GREEN_STAINED_GLASS_PANE, 0));
        beaconSpace.add(new ColoredBlock(DyeColor.RED.getTextureDiffuseColor(), 0, Blocks.RED_STAINED_GLASS_PANE, 0));
        beaconSpace.add(new ColoredBlock(DyeColor.BLACK.getTextureDiffuseColor(), 0, Blocks.BLACK_STAINED_GLASS_PANE, 0));
    }

    /**
     * 添加地图方块，如果有相同地图颜色的方块，选择bc颜色更接近地图颜色的那个
     */
    private static void addMBlock(int mapRgb, float diff, SelectBlock block, int y, Map<Integer, Tuple<Float, ColoredBlock>> colorTracker) {
        if (mapRgb == 0) {
            return;
        }
        Tuple<Float, ColoredBlock> prev = colorTracker.get(mapRgb);
        if (prev == null) {
            colorTracker.put(mapRgb, new Tuple<>(diff, new ColoredBlock(mapRgb, block.lightWeight, block.block, y)));
        } else if (prev.getA() > diff) {
            colorTracker.put(mapRgb, new Tuple<>(diff, new ColoredBlock(mapRgb, block.lightWeight, block.block, y)));
        }
    }

    public static void reBuildAll() {
        blockSpace.clear();
        mapSpace.clear();
        map0Space.clear();
        mapUpSpace.clear();
        mapDownSpace.clear();
        beaconSpace.clear();
        buildAll();
    }

    public static boolean allLoad() {
        return blockSpace.load && mapSpace.load && map0Space.load && mapUpSpace.load && mapDownSpace.load;
    }
}