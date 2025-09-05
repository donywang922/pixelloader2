package com.ywsuoyi.imgLoader;

import com.ywsuoyi.Setting;
import com.ywsuoyi.SettingScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ImgSettingScreen extends SettingScreen {
    public Button imgsize, pm;


    @Override
    public void init() {
        super.init();
        imgsize = this.addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.imgsize", Setting.imgSize),
                p_onPress_1_ -> {
                    Setting.imgSize++;
                    Setting.imgSize = Setting.imgSize > 8 ? 1 : Setting.imgSize;
                    imgsize.setMessage(Component.translatable("pixelLoader.screen.imgsize", Setting.imgSize));
                }).bounds(this.width / 2 - 100, height / 2 - 12, 200, 20).build());
        pm = this.addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.pm." + Setting.flat),
                p_onPress_1_ -> {
                    Setting.flat = !Setting.flat;
                    pm.setMessage(Component.translatable("pixelLoader.screen.pm." + Setting.flat));
                }).bounds(this.width / 2 - 100, height / 2 + 12, 200, 20).build());
    }
}
