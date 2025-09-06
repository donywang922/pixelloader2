package com.ywsuoyi.colorspace;

import com.ywsuoyi.guiComponent.SuggestionEditBox;
import com.ywsuoyi.loadingThreadUtil.BaseThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class ColorSettingScreen extends Screen {
    Button type, load, stop, filter, place, edit, read, write;
    SuggestionEditBox colorFile;
    Component message = Component.empty();
    Player player;

    protected ColorSettingScreen(Player player) {
        super(Component.translatable("pixelLoader.screen.colorspace"));
        this.player = player;
    }

    @Override
    protected void init() {
        List<String> suggestions = ColorSpaceFileManager.getAvailableColorSpaceFiles();
        type = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.colorspace.filtertype." + ColorSpaces.whiteList),
                p_onPress_1_ -> {
                    ColorSpaces.whiteList = !ColorSpaces.whiteList;
                    type.setMessage(Component.translatable("pixelLoader.screen.colorspace.filtertype." + ColorSpaces.whiteList));
                }).bounds(20, 20, 100, 20).build());
        filter = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.colorspace.openfilter"),
                p_onPress_1_ -> ColorSpaces.openFilter = true).bounds(20, 44, 100, 20).build());

        read = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.colorspace.read"),
                p_onPress_1_ -> {
                    String fileName = colorFile.getValue().trim();
                    if (!fileName.isEmpty()) {
                        boolean success = ColorSpaceFileManager.loadColorSpace(fileName);
                        if (success) {
                            type.setMessage(Component.translatable("pixelLoader.screen.colorspace.filtertype." + ColorSpaces.whiteList));
                            // 重新构建色域
                            ColorSpaces.reBuildAll();
                            message = Component.translatable("pixelLoader.screen.colorspace.message.load_success", fileName);
                        } else {
                            message = Component.translatable("pixelLoader.screen.colorspace.message.load_error", fileName);
                        }
                    } else {
                        message = Component.translatable("pixelLoader.screen.colorspace.message.empty");
                    }
                }).bounds(20, 130, 100, 20).build());
        write = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.colorspace.write"),
                p_onPress_1_ -> {
                    String fileName = colorFile.getValue().trim();
                    if (!fileName.isEmpty()) {
                        boolean success = ColorSpaceFileManager.saveColorSpace(fileName);
                        if (success) {
                            // 刷新建议列表
                            List<String> newSuggestions = ColorSpaceFileManager.getAvailableColorSpaceFiles();
                            colorFile.updateSuggestions(newSuggestions);
                            message = Component.translatable("pixelLoader.screen.colorspace.message.save_success", fileName);
                        } else {
                            message = Component.translatable("pixelLoader.screen.colorspace.message.save_error", fileName);
                        }
                    } else {
                        message = Component.translatable("pixelLoader.screen.colorspace.message.empty");
                    }
                }).bounds(20, 154, 100, 20).build());

        load = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.colorspace.load"),
                p_onPress_1_ -> {
                    ColorSpaces.thread = new LoadColorSpaceThread(player, player.level());
                    ColorSpaces.thread.start();
                }).bounds(width - 120, 20, 100, 20).build());
        edit = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.colorspace.edit"),
                p_onPress_1_ -> Minecraft.getInstance().setScreen(new SelectBlockScreen())).bounds(width - 120, 70, 100, 20).build());
        place = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.colorspace.place"),
                p_onPress_1_ -> {
                    ColorSpaces.waitPlace = true;
                    onClose();
                }).bounds(width - 120, 94, 100, 20).build());
        stop = addRenderableWidget(Button.builder(
                Component.translatable("pixelLoader.screen.colorspace.forcestop"), p_onPress_1_ -> {
                    if (ColorSpaces.thread != null) {
                        ColorSpaces.thread.forceStop();
                    }
                }).bounds(width - 120, height - 40, 100, 20).build());
        stop.visible = place.visible = edit.visible = false;

        colorFile = addRenderableWidget(new SuggestionEditBox(font, 20, 100, 100, 20,
                Component.translatable("pixelLoader.screen.colorspace.colorfile"), suggestions));
        colorFile.setValue(ColorSpaces.fileName);
        colorFile.setResponder(s -> ColorSpaces.fileName = s);

    }

    @Override
    public void tick() {
        super.tick();
        load.visible = ColorSpaces.thread == null || ColorSpaces.thread.state == BaseThread.State.end;
        stop.visible = ColorSpaces.thread != null && ColorSpaces.thread.state == BaseThread.State.run;
        place.visible = edit.visible = ColorSpaces.allLoad();
    }

    @Override
    public void render(GuiGraphics poseStack, int i, int j, float f) {
        super.render(poseStack, i, j, f);
        poseStack.drawString(this.font, Component.translatable("pixelLoader.screen.colorspace.file"), 20, 82, 0xFFFFFF);
        MutableComponent msg = Component.translatable("pixelLoader.screen.colorspace.message",
                ColorSpaces.thread != null ? ColorSpaces.thread.message : Component.empty());
        poseStack.drawString(this.font, msg, this.width - 120, 45, 0xFFFFFF);
        MutableComponent cnt = Component.translatable("pixelLoader.screen.colorspace.count", ColorSpaces.selectBlocks.size());
        poseStack.drawString(this.font, cnt, this.width - 120, 55, 0xFFFFFF);
        poseStack.drawString(this.font, message, 20, 180, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
