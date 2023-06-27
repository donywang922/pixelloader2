package com.ywsuoyi.pixelloader;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class ProjectorScreen extends Screen {
    public BlockPos pos;

    protected ProjectorScreen(BlockPos pos) {
        super(Component.translatable("pixelLoader.projector.screen"));
        this.pos = pos;
    }
}
