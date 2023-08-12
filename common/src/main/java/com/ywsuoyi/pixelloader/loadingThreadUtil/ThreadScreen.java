package com.ywsuoyi.pixelloader.loadingThreadUtil;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ywsuoyi.pixelloader.colorspace.ColorSpace;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class ThreadScreen extends Screen {
    public ThreadData data;
    public Button place;

    protected ThreadScreen(BlockPos pos) {
        super(Component.translatable("pixelLoader.screen.thread"));
        data = ThreadData.data.get(pos);
    }

    @Override
    protected void init() {
        place = this.addRenderableWidget(new Button(20, 20, 100, 20,
                Component.translatable("pixelLoader.screen.thread.place"), p_onPress_1_ -> {
            if (data.thread.state == BaseThread.State.end) {
                data.state = ThreadData.State.place;
                onClose();
            }
        }));
        place.visible = false;
    }

    @Override
    public void tick() {
        super.tick();
        place.visible = data != null && data.thread.state == BaseThread.State.end;
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        super.render(poseStack, i, j, f);
        if (data != null) {
            drawString(poseStack, this.font, Component.literal(data.thread.file.getName()), 20, 40, 0xFFFFFF);
            drawString(poseStack, this.font, data.thread.message, 20, 50, 0xFFFFFF);
            drawString(poseStack, this.font, Component.translatable("pixelLoader.screen.thread.center", data.thread.center), 20, 60, 0xFFFFFF);
        } else {
            drawString(poseStack, this.font, Component.literal("no data"), 20, 40, 0xFFFFFF);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
