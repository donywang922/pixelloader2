package com.ywsuoyi.loader.mapLoader;

import com.ywsuoyi.Setting;
import com.ywsuoyi.loader.AbstractLoader;
import com.ywsuoyi.loadingThreadUtil.BaseThread;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.context.UseOnContext;

public class MapLoader extends AbstractLoader {


    public MapLoader(Properties properties) {
        super(properties);
    }

    @Override
    public BaseThread getThread(UseOnContext context) {
        return new LoadMapThread(
                context.getPlayer(),
                Setting.getImg(),
                Setting.dither,
                Setting.mapSize,
                Setting.cutout,
                context.getLevel(),
                context.getClickedPos().offset(context.getClickedFace().getNormal()),
                Setting.mapMode
        );
    }

    @Override
    public Screen getScreen() {
        return new MapSettingScreen();
    }
}
