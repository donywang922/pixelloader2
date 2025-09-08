package com.ywsuoyi.loader.mapLoader;

import com.ywsuoyi.loader.AbstractLoader;
import com.ywsuoyi.loadingThreadUtil.BaseThread;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;

import java.io.File;

public class MapLoader extends AbstractLoader {


    public MapLoader(Properties properties) {
        super(properties);
    }

    @Override
    public BaseThread getThread(UseOnContext context) {
        return new LoadMapThread(
                context.getPlayer(),
                MapSetting.getImg(),
                MapSetting.dither,
                MapSetting.mapSize,
                MapSetting.cutout,
                context.getLevel(),
                context.getClickedPos().offset(context.getClickedFace().getNormal()),
                MapSetting.mapMode
        );
    }

    @Override
    public Screen getScreen() {
        return new MapSettingScreen();
    }

    @Override
    public void addindex(Player player) {
        MapSetting.addindex(player);
    }

    @Override
    public File getImg() {
        return MapSetting.getImg();
    }
}
