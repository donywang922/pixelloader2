package com.ywsuoyi.pixelloader.loadingThreadUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadData {
    public static HashMap<BlockPos, ThreadData> data = new HashMap<>();
    public State state = State.loading;
    public LoadingThread thread;
    public ConcurrentHashMap<BlockPos, BlockState> genBlocks = new ConcurrentHashMap<>();

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
