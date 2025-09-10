package com.ywsuoyi.loader.beaconLoader;

import com.ywsuoyi.ImageManager;
import com.ywsuoyi.Selections;
import com.ywsuoyi.guiComponent.IntegerEditBox;
import com.ywsuoyi.guiComponent.SelectionOnlyBox;
import com.ywsuoyi.loader.mapLoader.MapSetting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class BeaconSettingScreen extends Screen {
    public SelectionOnlyBox imgFile, dither, onfinish;
    public IntegerEditBox size;
    Component widthText = Component.translatable("pixelLoader.setting.screen.pixel.width");
    int w;

    public BeaconSettingScreen() {
        super(Component.translatable("pixelLoader.setting.screen"));
    }

    @Override
    public void init() {
        super.init();
        imgFile = this.addRenderableWidget(new SelectionOnlyBox(font, 20, 20, width - 40, 20,
                Component.translatable("pixelLoader.setting.screen.file"), ImageManager.getImageListStr()));
        imgFile.setOptionIndex(BeaconSetting.index);

        w = (width - 40 - 4 * 3) / 4;

        int tmp = font.width(widthText.getVisualOrderText());

        dither = this.addRenderableWidget(new SelectionOnlyBox(font, 20 + w * 2 + 8, 44, w, 20,
                Component.translatable("pixelLoader.setting.screen.dither"), Selections.dither));
        size = addRenderableWidget(new IntegerEditBox(font, 20 + tmp + 4, 44, w * 2 - tmp, 20,
                Component.translatable("pixelLoader.setting.screen.map.size"), 512, 1));
        size.setValue(String.valueOf(BeaconSetting.size));
        onfinish = addRenderableWidget(new SelectionOnlyBox(font, 20 + w * 3 + 12, 44, w, 20,
                Component.translatable("pixelLoader.setting.screen.onfinish"), Selections.onfinish));
        onfinish.setOptionIndex(MapSetting.onfinish);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);

        guiGraphics.drawString(this.font, widthText, 20, 50, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        super.onClose();
        BeaconSetting.size = Integer.parseInt(size.getValue());
        BeaconSetting.dither = dither.getOptionIndex();
        BeaconSetting.onfinish = onfinish.getOptionIndex();
        BeaconSetting.index = imgFile.getOptionIndex();
    }
}
