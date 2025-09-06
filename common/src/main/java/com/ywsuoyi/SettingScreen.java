package com.ywsuoyi;

import com.ywsuoyi.guiComponent.SelectionOnlyBox;
import com.ywsuoyi.loadingThreadUtil.BaseThread;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingScreen extends Screen {
    public SelectionOnlyBox imgFile, cutout, dither;

    public SettingScreen() {
        super(Component.translatable("pixelLoader.screen"));
    }

    @Override
    public void init() {
        Setting.updateFileList();
        List<String> suggestions = new java.util.ArrayList<>(Setting.imglist.stream().map(File::getName).toList());
        if (suggestions.isEmpty()) {
            suggestions.add(Component.translatable("pixelLoader.noFile").toString());
        }
        imgFile = this.addRenderableWidget(new SelectionOnlyBox(font, 20, 20, width - 40, 20,
                Component.translatable("pixelLoader.screen.file"), suggestions));
        imgFile.setSelectedIndex(Setting.index);
        imgFile.setResponder(s -> Setting.index = imgFile.getSelectedIndex());

        int w = (width - 48) / 3;

        List<String> cutout_suggestions = new ArrayList<>(3);
        cutout_suggestions.add(Component.translatable("pixelLoader.screen.cutout.0").getString());
        cutout_suggestions.add(Component.translatable("pixelLoader.screen.cutout.1").getString());
        cutout_suggestions.add(Component.translatable("pixelLoader.screen.cutout.2").getString());
        cutout = this.addRenderableWidget(new SelectionOnlyBox(font, 20, 44, w, 20,
                Component.translatable("pixelLoader.screen.cutout"), cutout_suggestions));
        cutout.setSelectedIndex(Setting.cutout);
        cutout.setResponder(s -> Setting.cutout = cutout.getSelectedIndex());

        List<String> dither_suggestions = new ArrayList<>(2);
        dither_suggestions.add(Component.translatable("pixelLoader.screen.dither.0").getString());
        dither_suggestions.add(Component.translatable("pixelLoader.screen.dither.1").getString());
        dither = this.addRenderableWidget(new SelectionOnlyBox(font, 20 + w + 4, 44, w, 20,
                Component.translatable("pixelLoader.screen.dither"), dither_suggestions));
        dither.setSelectedIndex(Setting.dither);
        dither.setResponder(s -> Setting.dither = dither.getSelectedIndex());

        this.addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.stop"),
                p_onPress_1_ -> BaseThread.stopAllThread()).bounds(20 + w * 2 + 8, 44, w, 20).build());


    }
}
