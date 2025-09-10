package com.ywsuoyi.colorspace;

import com.ywsuoyi.Selections;
import com.ywsuoyi.guiComponent.NumberEditBox;
import com.ywsuoyi.guiComponent.SelectionOnlyBox;
import com.ywsuoyi.guiComponent.SelectionEditBox;
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
    Button load, stop, filter, place, edit, read, write;
    SelectionEditBox colorFile;
    SelectionOnlyBox type;
    NumberEditBox light;
    Component message = Component.empty();
    Player player;

    protected ColorSettingScreen(Player player) {
        super(Component.translatable("pixelLoader.colorspace.screen"));
        this.player = player;
    }

    @Override
    protected void init() {
        List<String> suggestions = ColorSpaceFileManager.getAvailableColorSpaceFiles();
        type = addRenderableWidget(new SelectionOnlyBox(font, 20, 20, 100, 20,
                Component.translatable("pixelLoader.colorspace.screen.whitelist"), Selections.whitelist));
        type.setOptionIndex(ColorSpaces.whiteList);
        type.setResponder(s -> ColorSpaces.whiteList = type.getOptionIndex());

        filter = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.colorspace.screen.openfilter"),
                p_onPress_1_ -> ColorSpaces.openFilter = true).bounds(20, 44, 100, 20).build());

        read = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.colorspace.screen.read"),
                p_onPress_1_ -> {
                    String fileName = colorFile.getValue().trim();
                    if (!fileName.isEmpty()) {
                        boolean success = ColorSpaceFileManager.loadColorSpace(fileName);
                        if (success) {
                            type.setOptionIndex(ColorSpaces.whiteList);
                            light.setValue(String.valueOf(ColorSpaces.lightWeight));
                            // 重新构建色域
                            ColorSpaces.reBuildAll();
                            message = Component.translatable("pixelLoader.colorspace.screen.message.load_success", fileName);
                        } else {
                            message = Component.translatable("pixelLoader.colorspace.screen.message.load_error", fileName);
                        }
                    } else {
                        message = Component.translatable("pixelLoader.colorspace.screen.message.empty");
                    }
                }).bounds(20, 130, 100, 20).build());
        write = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.colorspace.screen.write"),
                p_onPress_1_ -> {
                    String fileName = colorFile.getValue().trim();
                    if (!fileName.isEmpty()) {
                        boolean success = ColorSpaceFileManager.saveColorSpace(fileName);
                        if (success) {
                            // 刷新建议列表
                            List<String> newSuggestions = ColorSpaceFileManager.getAvailableColorSpaceFiles();
                            colorFile.updateOptions(newSuggestions);
                            message = Component.translatable("pixelLoader.colorspace.screen.message.save_success", fileName);
                        } else {
                            message = Component.translatable("pixelLoader.colorspace.screen.message.save_error", fileName);
                        }
                    } else {
                        message = Component.translatable("pixelLoader.colorspace.screen.message.empty");
                    }
                }).bounds(20, 154, 100, 20).build());

        load = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.colorspace.screen.load"),
                p_onPress_1_ -> {
                    ColorSpaces.thread = new LoadColorSpaceThread(player, player.level());
                    ColorSpaces.thread.start();
                }).bounds(width - 120, 60, 100, 20).build());
        edit = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.colorspace.screen.edit"),
                p_onPress_1_ -> Minecraft.getInstance().setScreen(new SelectBlockScreen())).bounds(width - 120, 115, 100, 20).build());
        place = addRenderableWidget(Button.builder(Component.translatable("pixelLoader.colorspace.screen.place"),
                p_onPress_1_ -> {
                    ColorSpaces.waitPlace = true;
                    onClose();
                }).bounds(width - 120, 139, 100, 20).build());
        stop = addRenderableWidget(Button.builder(
                Component.translatable("pixelLoader.colorspace.screen.forcestop"), p_onPress_1_ -> {
                    if (ColorSpaces.thread != null) {
                        ColorSpaces.thread.forceStop();
                    }
                }).bounds(width - 120, 60, 100, 20).build());
        stop.visible = place.visible = edit.visible = false;

        colorFile = addRenderableWidget(new SelectionEditBox(font, 20, 100, 100, 20,
                Component.translatable("pixelLoader.colorspace.screen.colorfile"), suggestions));
        colorFile.setValue(ColorSpaces.fileName);
        colorFile.setResponder(s -> ColorSpaces.fileName = s);

        light = addRenderableWidget(new NumberEditBox(font, width - 120, 30, 100, 20, Component.translatable("pixelLoader.colorspace.screen.light")));
        light.setValue(String.valueOf(ColorSpaces.lightWeight));
        light.setResponder(s -> {
            try {
                float lightWeight = Float.parseFloat(s);
                float clampLightWeight = Math.clamp(lightWeight, 0, 15);
                ColorSpaces.lightWeight = lightWeight;
                if (lightWeight != clampLightWeight)
                    light.setValue(String.valueOf(clampLightWeight));
            } catch (Exception e) {
                light.setValue("0");
            }
        });

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
        poseStack.drawString(this.font, Component.translatable("pixelLoader.colorspace.screen.file"), 20, 82, 0xFFFFFF);
        MutableComponent msg = Component.translatable("pixelLoader.colorspace.screen.message",
                ColorSpaces.thread != null ? ColorSpaces.thread.message : Component.empty());
        poseStack.drawString(this.font, msg, this.width - 120, 85, 0xFFFFFF);
        MutableComponent cnt = Component.translatable("pixelLoader.colorspace.screen.count", ColorSpaces.selectBlocks.size());
        poseStack.drawString(this.font, cnt, this.width - 120, 95, 0xFFFFFF);
        poseStack.drawString(this.font, message, 20, 180, 0xFFFFFF);
        poseStack.drawString(this.font, Component.translatable("pixelLoader.colorspace.screen.light"), width - 120, 20, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
