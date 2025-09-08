package com.ywsuoyi.loader.mapLoader;

import com.ywsuoyi.loader.SettingScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class MapSettingScreen extends SettingScreen {
    public Button mapsize, lt;

    @Override
    public void init() {
        super.init();
        mapsize = this.addRenderableWidget(Button.builder(Component.translatable("pixelLoader.setting.screen.mapsize", MapSetting.mapSize),
                p_onPress_1_ -> {
                    MapSetting.mapSize++;
                    MapSetting.mapSize = MapSetting.mapSize > 8 ? 1 : MapSetting.mapSize;
                    mapsize.setMessage(Component.translatable("pixelLoader.setting.screen.mapsize", MapSetting.mapSize));
                }).bounds(this.width / 2 - 100, height / 2 - 12, 200, 20).build());
        lt = this.addRenderableWidget(Button.builder(Component.translatable("pixelLoader.setting.screen.mapmode." + MapSetting.mapMode),
                p_onPress_1_ -> {
                    if (MapSetting.mapMode == MapSetting.MapMode.flat) MapSetting.mapMode = MapSetting.MapMode.threeD;
                    else if (MapSetting.mapMode == MapSetting.MapMode.threeD) MapSetting.mapMode = MapSetting.MapMode.cover;
                    else if (MapSetting.mapMode == MapSetting.MapMode.cover) MapSetting.mapMode = MapSetting.MapMode.cover_c;
                    else if (MapSetting.mapMode == MapSetting.MapMode.cover_c) MapSetting.mapMode = MapSetting.MapMode.cover_c2;
                    else MapSetting.mapMode = MapSetting.MapMode.flat;
                    lt.setMessage(Component.translatable("pixelLoader.setting.screen.mapmode." + MapSetting.mapMode));
                }).bounds(this.width / 2 - 100, height / 2 + 12, 200, 20).build());
    }
}
