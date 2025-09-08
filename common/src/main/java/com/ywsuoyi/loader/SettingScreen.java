package com.ywsuoyi.loader;

import com.ywsuoyi.ImageManager;
import com.ywsuoyi.Selections;
import com.ywsuoyi.guiComponent.SelectionOnlyBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SettingScreen extends Screen {
    public SelectionOnlyBox imgFile, cutout, dither, support, cover;

    public SettingScreen() {
        super(Component.translatable("pixelLoader.setting.screen"));
    }

    @Override
    public void init() {
        imgFile = this.addRenderableWidget(new SelectionOnlyBox(font, 20, 20, width - 40, 20,
                Component.translatable("pixelLoader.setting.screen.file"), ImageManager.getImageListStr()));

        int w = (width - 40 - 4 * 3) / 4;

        cutout = this.addRenderableWidget(new SelectionOnlyBox(font, 20, 44, w, 20,
                Component.translatable("pixelLoader.setting.screen.cutout"), Selections.cutout));

        dither = this.addRenderableWidget(new SelectionOnlyBox(font, 20 + w + 4, 44, w, 20,
                Component.translatable("pixelLoader.setting.screen.dither"), Selections.dither));

        support = this.addRenderableWidget(new SelectionOnlyBox(font, 20 + w * 2 + 8, 44, w, 20,
                Component.translatable("pixelLoader.setting.screen.support"), Selections.support));

        cover = this.addRenderableWidget(new SelectionOnlyBox(font, 20 + w * 3 + 12, 44, w, 20,
                Component.translatable("pixelLoader.setting.screen.cover"), Selections.cover));
    }
}
