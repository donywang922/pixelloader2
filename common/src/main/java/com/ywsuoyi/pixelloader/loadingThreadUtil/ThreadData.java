package com.ywsuoyi.pixelloader.loadingThreadUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.LinkedList;

public class ThreadData {
    public static HashMap<BlockPos, ThreadData> data = new HashMap<>();
    public State state = State.loading;
    public LoadingThread thread;
    public LinkedList<Tuple<BlockPos, BlockState>> genBlocks = new LinkedList<>();
    public Direction[] directions = Direction.values();
    public float renderPercentage = 0;
    public boolean autoLowerPercentage = true;
    public BlockPos center;

    public ThreadData(LoadingThread thread) {
        this.thread = thread;
    }

    public enum State {
        loading, place, save
    }

    public static ThreadData getData(LoadingThread thread) {
        if (data.containsKey(thread.anchor)) return data.get(thread.anchor);
        ThreadData threadData = new ThreadData(thread);
        data.put(thread.anchor, threadData);
        return threadData;
    }

    public static ThreadData getData(BlockPos pos) {
        return data.get(pos);
    }
}
