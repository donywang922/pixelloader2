package com.ywsuoyi;

import com.ywsuoyi.loadingThreadUtil.BaseThread;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SettingScreen extends Screen {
    public Button fs, cutout;

    public SettingScreen() {
        super(Component.translatable("pixelLoader.screen"));
    }

    @Override
    public void init() {
        cutout = this.addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.cutout." + Setting.cutout),
                p_onPress_1_ -> {
                    Setting.cutout++;
                    Setting.cutout = Setting.cutout > 3 ? 0 : Setting.cutout;
                    cutout.setMessage(Component.translatable("pixelLoader.screen.cutout." + Setting.cutout));
                }).bounds(this.width / 2 - 100, height / 2 - 60, 200, 20).build());
        fs = this.addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.fs." + Setting.dither),
                p_onPress_1_ -> {
                    Setting.dither = !Setting.dither;
                    fs.setMessage(Component.translatable("pixelLoader.screen.fs." + Setting.dither));
                }).bounds(this.width / 2 - 100, height / 2 - 36, 200, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.stop"),
                p_onPress_1_ -> BaseThread.stopAllThread()).bounds(this.width / 2 - 100, height / 2 + 36, 200, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.close"),
                p_onPress_1_ -> this.onClose()).bounds(
                this.width / 2 - 100, height / 2 + 60, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics matrixStack, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        renderBackground(matrixStack);
        super.render(matrixStack, p_230430_2_, p_230430_3_, p_230430_4_);
    }
}
