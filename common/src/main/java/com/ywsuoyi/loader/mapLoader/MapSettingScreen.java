package com.ywsuoyi.loader.mapLoader;

import com.ywsuoyi.Setting;
import com.ywsuoyi.SettingScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class MapSettingScreen extends SettingScreen {
    public Button mapsize, lt;

    @Override
    public void init() {
        super.init();
        mapsize = this.addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.mapsize", Setting.mapSize),
                p_onPress_1_ -> {
                    Setting.mapSize++;
                    Setting.mapSize = Setting.mapSize > 8 ? 1 : Setting.mapSize;
                    mapsize.setMessage(Component.translatable("pixelLoader.screen.mapsize", Setting.mapSize));
                }).bounds(this.width / 2 - 100, height / 2 - 12, 200, 20).build());
        lt = this.addRenderableWidget(Button.builder(Component.translatable("pixelLoader.screen.mapmode." + Setting.mapMode),
                p_onPress_1_ -> {
                    if (Setting.mapMode == Setting.MapMode.flat) Setting.mapMode = Setting.MapMode.threeD;
                    else if (Setting.mapMode == Setting.MapMode.threeD) Setting.mapMode = Setting.MapMode.cover;
                    else if (Setting.mapMode == Setting.MapMode.cover) Setting.mapMode = Setting.MapMode.cover_c;
                    else if (Setting.mapMode == Setting.MapMode.cover_c) Setting.mapMode = Setting.MapMode.cover_c2;
                    else Setting.mapMode = Setting.MapMode.flat;
                    lt.setMessage(Component.translatable("pixelLoader.screen.mapmode." + Setting.mapMode));
                }).bounds(this.width / 2 - 100, height / 2 + 12, 200, 20).build());
    }
}
