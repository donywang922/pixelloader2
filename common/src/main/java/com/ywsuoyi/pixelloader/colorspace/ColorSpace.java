package com.ywsuoyi.pixelloader.colorspace;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;

public class ColorSpace {
    public static final TreeColorSpace blockSpace = new TreeColorSpace();
    public static final TreeColorSpace mapSpace = new TreeColorSpace();
    public static final TreeColorSpace map0Space = new TreeColorSpace();
    public static final TreeColorSpace mapDownSpace = new TreeColorSpace();
    public static final TreeColorSpace mapUpSpace = new TreeColorSpace();
    public static final ArrayColorSpace beaconSpace = new ArrayColorSpace();

    public static LoadColorSpaceThread thread;

    public static NonNullList<ItemStack> filter = NonNullList.withSize(54, ItemStack.EMPTY);
    public static NonNullList<SelectBlock> selectBlocks = NonNullList.create();
    public static boolean whiteList = false;

    public static boolean waitPlace = false;
    public static boolean openFilter = false;

    public static void clearAll() {
        selectBlocks.clear();
        blockSpace.clear();
        mapSpace.clear();
        map0Space.clear();
        mapUpSpace.clear();
        mapDownSpace.clear();
        beaconSpace.clear();
    }

    public static void buildAll() {
        Set<Integer> colSet = new HashSet<>();
        Set<Integer> col0Set = new HashSet<>();
        Set<Integer> coldSet = new HashSet<>();
        Set<Integer> coluSet = new HashSet<>();
        for (SelectBlock block : selectBlocks) {
            if (!block.active) continue;
            blockSpace.blocks.add(new ColoredBlock(block.bc.rgb, block.block, 0));

            addMBlock(block.map.rgb, block.block, 0, mapSpace, colSet);
            addMBlock(block.mapB.rgb, block.block, -1, mapSpace, colSet);
            addMBlock(block.mapT.rgb, block.block, 1, mapSpace, colSet);

            addMBlock(block.map.rgb, block.block, 0, map0Space, col0Set);
            addMBlock(block.mapB.rgb, block.block, -1, mapDownSpace, coldSet);
            addMBlock(block.mapT.rgb, block.block, 1, mapUpSpace, coluSet);
        }
        blockSpace.build();
        mapSpace.build();
        map0Space.build();
        mapDownSpace.build();
        mapUpSpace.build();
        beaconSpace.build();
    }

    private static void addMBlock(int rgb, Block block, int y, AbstractColorSpace space, Set<Integer> his) {
        if (!his.contains(rgb)) {
            space.blocks.add(new ColoredBlock(rgb, block, y));
            his.add(rgb);
        }
    }

    public static void reBuildAll() {
        clearAll();
        buildAll();
    }

    public static boolean allLoad() {
        return blockSpace.load && mapSpace.load && map0Space.load && mapUpSpace.load && mapDownSpace.load;
    }

}
