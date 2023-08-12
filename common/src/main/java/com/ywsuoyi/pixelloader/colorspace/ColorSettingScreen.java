package com.ywsuoyi.pixelloader.colorspace;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ywsuoyi.pixelloader.loadingThreadUtil.BaseThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class ColorSettingScreen extends Screen {
    Button type, load, stop, filter, place, placeF;
    Player player;

    protected ColorSettingScreen(Player player) {
        super(Component.translatable("pixelLoader.screen.colorspace"));
        this.player = player;
    }

    @Override
    protected void init() {
        type = this.addRenderableWidget(new Button(this.width / 2 - 150, height / 2 - 60, 100, 20,
                Component.translatable("pixelLoader.screen.colorspace.blacklist." + ColorSpace.blackList), p_onPress_1_ -> {
            ColorSpace.blackList = !ColorSpace.blackList;
            type.setMessage(Component.translatable("pixelLoader.screen.colorspace.blacklist." + ColorSpace.blackList));
        }));
        filter = this.addRenderableWidget(new Button(this.width / 2 - 150, height / 2 - 36, 100, 20,
                Component.translatable("pixelLoader.screen.colorspace.openfilter"), p_onPress_1_ -> {
            ColorSpace.openFilter = true;
        }));

        load = this.addRenderableWidget(new Button(this.width / 2 + 50, height / 2 - 60, 100, 20,
                Component.translatable("pixelLoader.screen.colorspace.load"), p_onPress_1_ -> {
            ColorSpace.thread = new LoadColorSpaceThread(player, player.level);
            ColorSpace.thread.start();
        }));

        place = this.addRenderableWidget(new Button(this.width / 2 + 50, height / 2 + 12, 100, 20,
                Component.translatable("pixelLoader.screen.colorspace.place"), p_onPress_1_ -> {
            ColorSpace.waitPlace = true;
            onClose();
        }));
        placeF = this.addRenderableWidget(new Button(this.width / 2 + 50, height / 2 + 36, 100, 20,
                Component.translatable("pixelLoader.screen.colorspace.placef"), p_onPress_1_ -> {
            Minecraft.getInstance().setScreen(new SelectBlockScreen());
        }));
        stop = this.addRenderableWidget(new Button(this.width / 2 + 50, height / 2 + 60, 100, 20,
                Component.translatable("pixelLoader.screen.colorspace.forcestop"), p_onPress_1_ -> {
            if (ColorSpace.thread != null) {
                ColorSpace.thread.forceStop();
            }
        }));
        stop.visible = place.visible = placeF.visible = false;
    }

    @Override
    public void tick() {
        super.tick();
        load.visible = ColorSpace.thread == null || ColorSpace.thread.state == BaseThread.State.end;
        stop.visible = ColorSpace.thread != null && ColorSpace.thread.state == BaseThread.State.run;
        place.visible = placeF.visible = ColorSpace.allLoad();
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        super.render(poseStack, i, j, f);
        MutableComponent msg = Component.translatable("pixelLoader.screen.colorspace.message",
                ColorSpace.thread != null ? ColorSpace.thread.message : Component.empty());
        drawString(poseStack, this.font, msg, this.width / 2 + 50, height / 2 - 36, 0xFFFFFF);
        MutableComponent cnt = Component.translatable("pixelLoader.screen.colorspace.count",
                ColorSpace.selectBlocks.size());
        drawString(poseStack, this.font, cnt, this.width / 2 + 50, height / 2 - 24, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
