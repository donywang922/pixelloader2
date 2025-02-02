package com.ywsuoyi.colorspace;

import com.ywsuoyi.loadingThreadUtil.BaseThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class ColorSettingScreen extends Screen {
    Button type, load, stop, filter, place, edit, read, write;
    EditBox colorFile;
    Player player;

    protected ColorSettingScreen(Player player) {
        super(Component.translatable("pixelLoader.screen.colorspace"));
        this.player = player;
    }

    @Override
    protected void init() {
        type = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.colorspace.filtertype." + ColorSpace.whiteList),
                p_onPress_1_ -> {
                    ColorSpace.whiteList = !ColorSpace.whiteList;
                    type.setMessage(Component.translatable("pixelLoader.screen.colorspace.filtertype." + ColorSpace.whiteList));
                }).bounds(20, 20, 100, 20).build());
        filter = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.colorspace.openfilter"),
                p_onPress_1_ -> ColorSpace.openFilter = true).bounds(20, 44, 100, 20).build());

        colorFile = addRenderableWidget(new EditBox(font, 20, 92, 100, 20, Component.translatable("pixelLoader.screen.colorspace.colorfile")));
        read = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.colorspace.read"),
                p_onPress_1_ -> {
                }).bounds(20, 116, 100, 20).build());
        write = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.colorspace.write"),
                p_onPress_1_ -> {
                }).bounds(20, 140, 100, 20).build());

        load = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.colorspace.load"),
                p_onPress_1_ -> {
                    ColorSpace.thread = new LoadColorSpaceThread(player, player.level());
                    ColorSpace.thread.start();
                }).bounds(width - 120, 20, 100, 20).build());
        edit = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.colorspace.edit"),
                p_onPress_1_ -> Minecraft.getInstance().setScreen(new SelectBlockScreen())).bounds(width - 120, 70, 100, 20).build());
        place = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.colorspace.place"),
                p_onPress_1_ -> {
                    ColorSpace.waitPlace = true;
                    onClose();
                }).bounds(width - 120, 94, 100, 20).build());
        stop = addRenderableWidget(Button.builder(
                Component.translatable("pixelLoader.screen.colorspace.forcestop"), p_onPress_1_ -> {
                    if (ColorSpace.thread != null) {
                        ColorSpace.thread.forceStop();
                    }
                }).bounds(width - 120, height - 40, 100, 20).build());
        stop.visible = place.visible = edit.visible = false;
    }

    @Override
    public void tick() {
        super.tick();
        load.visible = ColorSpace.thread == null || ColorSpace.thread.state == BaseThread.State.end;
        stop.visible = ColorSpace.thread != null && ColorSpace.thread.state == BaseThread.State.run;
        place.visible = edit.visible = ColorSpace.allLoad();
    }

    @Override
    public void render(GuiGraphics poseStack, int i, int j, float f) {
        super.render(poseStack, i, j, f);
        poseStack.drawString(this.font, Component.literal("TODO"), 20, 82, 0xFFFFFF);
        MutableComponent msg = Component.translatable("pixelLoader.screen.colorspace.message",
                ColorSpace.thread != null ? ColorSpace.thread.message : Component.empty());
        poseStack.drawString(this.font, msg, this.width - 120, 45, 0xFFFFFF);
        MutableComponent cnt = Component.translatable("pixelLoader.screen.colorspace.count",
                ColorSpace.selectBlocks.size());
        poseStack.drawString(this.font, cnt, this.width - 120, 55, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
