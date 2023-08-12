package com.ywsuoyi.pixelloader.loadingThreadUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayDeque;

public class BaseThread extends Thread {
    public static final ArrayDeque<BaseThread> threads = new ArrayDeque<>();
    public Player player;
    public State state = State.wait;
    public Component message = Component.empty();

    public BaseThread(Player player) {
        this.player = player;
    }

    @Override
    public synchronized void start() {
        state = State.run;
        setMessage(Component.translatable("pixelLoader.LoadingThread.start"));
        super.start();
    }

    public void onend(boolean force) {
        state = State.end;
        setMessage(force ? Component.translatable("pixelLoader.LoadingThread.stop") : Component.translatable("pixelLoader.LoadingThread.finish"));
        startNextThread();
    }

    public void setMessage(Component message) {
        if (player != null)
            player.displayClientMessage(message, true);
        this.message = message;
    }

    public void forceStop() {
        state = State.end;
    }

    public enum State {
        wait, run, end
    }

    public static void addThread(BaseThread thread) {
        threads.add(thread);
        startNextThread();
    }

    public static void startNextThread() {
        while (!threads.isEmpty()) {
            if (threads.getFirst().state == State.end)
                threads.removeFirst();
            else break;
        }
        if (threads.isEmpty()) return;
        BaseThread thread = threads.getFirst();
        if (thread.state == State.wait)
            thread.start();
    }

    public static void stopAllThread() {
        BaseThread thread = threads.getFirst();
        threads.clear();
        thread.forceStop();
    }
}
