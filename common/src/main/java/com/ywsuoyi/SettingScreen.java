package com.ywsuoyi;

import com.ywsuoyi.guiComponent.SelectionOnlyBox;
import com.ywsuoyi.loadingThreadUtil.BaseThread;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SettingScreen extends Screen {
    public SelectionOnlyBox imgFile, cutout, dither;

    public SettingScreen() {
        super(Component.translatable("pixelLoader.setting.screen"));
    }

    @Override
    public void init() {
        imgFile = this.addRenderableWidget(new SelectionOnlyBox(font, 20, 20, width - 40, 20,
                Component.translatable("pixelLoader.setting.screen.file"), ImageManager.getImageListStr()));
        imgFile.setSelectedIndex(Setting.index);
        imgFile.setResponder(s -> Setting.index = imgFile.getSelectedIndex());

        int w = (width - 48) / 3;

        cutout = this.addRenderableWidget(new SelectionOnlyBox(font, 20, 44, w, 20,
                Component.translatable("pixelLoader.setting.screen.cutout"), Selections.cutout));
        cutout.setSelectedIndex(Setting.cutout);
        cutout.setResponder(s -> Setting.cutout = cutout.getSelectedIndex());

        dither = this.addRenderableWidget(new SelectionOnlyBox(font, 20 + w + 4, 44, w, 20,
                Component.translatable("pixelLoader.setting.screen.dither"), Selections.dither));
        dither.setSelectedIndex(Setting.dither);
        dither.setResponder(s -> Setting.dither = dither.getSelectedIndex());

        this.addRenderableWidget(Button.builder(Component.translatable("pixelLoader.setting.screen.stop"),
                p_onPress_1_ -> BaseThread.stopAllThread()).bounds(20 + w * 2 + 8, 44, w, 20).build());


    }
}
