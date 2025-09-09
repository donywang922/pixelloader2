package com.ywsuoyi.loader.mapLoader;

import com.ywsuoyi.Selections;
import com.ywsuoyi.guiComponent.IntegerEditBox;
import com.ywsuoyi.guiComponent.SelectionOnlyBox;
import com.ywsuoyi.loader.SettingScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class MapSettingScreen extends SettingScreen {
    public IntegerEditBox size;
    public SelectionOnlyBox mode, fit, onfinish;
    int w;

    @Override
    public void init() {
        super.init();
        dither.setOptionIndex(MapSetting.dither);
        support.setOptionIndex(MapSetting.support);
        cutout.setOptionIndex(MapSetting.cutout);
        imgFile.setOptionIndex(MapSetting.index);
        cover.setOptionIndex(MapSetting.cover);

        w = (width - 40 - 4 * 3) / 4;
        size = addRenderableWidget(new IntegerEditBox(font, 20, 70, w, 20,
                Component.translatable("pixelLoader.setting.screen.map.size"), 8, 1));
        size.setValue(String.valueOf(MapSetting.size));
        fit = this.addRenderableWidget(new SelectionOnlyBox(font, 20 + w * 2 + 8, 70, w, 20,
                Component.translatable("pixelLoader.setting.screen.map.fit"), Selections.mapFit));
        fit.setOptionIndex(MapSetting.fit);
        onfinish = addRenderableWidget(new SelectionOnlyBox(font, 20 + w * 3 + 12, 70, w, 20,
                Component.translatable("pixelLoader.setting.screen.onfinish"), Selections.onfinish));
        onfinish.setOptionIndex(MapSetting.onfinish);
        mode = this.addRenderableWidget(new SelectionOnlyBox(font, 20, 96, width - 40, 20,
                Component.translatable("pixelLoader.setting.screen.map.mode"), Selections.mapMode));
        mode.setOptionIndex(MapSetting.mode.ordinal());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        int sizeI = Integer.parseInt(size.getValue());
        guiGraphics.drawString(this.font, Component.translatable("pixelLoader.setting.screen.map.hint", sizeI, sizeI * sizeI),
                20 + w + 4, 76, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        super.onClose();
        MapSetting.cutout = cutout.getOptionIndex();
        MapSetting.support = support.getOptionIndex();
        MapSetting.dither = dither.getOptionIndex();
        MapSetting.cover = cover.getOptionIndex();
        MapSetting.onfinish = onfinish.getOptionIndex();
        MapSetting.mode = MapSetting.MapMode.values()[mode.getOptionIndex()];
        MapSetting.size = Integer.parseInt(size.getValue());
        MapSetting.fit = fit.getOptionIndex();
        MapSetting.index = imgFile.getOptionIndex();
    }
}
