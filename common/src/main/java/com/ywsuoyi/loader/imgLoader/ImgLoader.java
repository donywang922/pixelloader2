package com.ywsuoyi.loader.imgLoader;

import com.ywsuoyi.loader.AbstractLoader;
import com.ywsuoyi.loadingThreadUtil.BaseThread;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;

import java.io.File;

public class ImgLoader extends AbstractLoader {


    public ImgLoader(Properties properties) {
        super(properties);
    }

    @Override
    public BaseThread getThread(UseOnContext context) {
        CoordinateSystem coordinateSystem = ImgSetting.getCoordinateSystem();
        return new LoadImgThread(
                context.getPlayer(),
                ImgSetting.getImg(),
                ImgSetting.dither,
                ImgSetting.cutout,
                ImgSetting.support,
                ImgSetting.cover,
                ImgSetting.onfinish,
                coordinateSystem,
                ImgSetting.getScale(),
                context.getLevel(),
                context.getClickedPos(),
                context.getClickedPos().offset(context.getClickedFace().getNormal())
        );
    }

    @Override
    public Screen getScreen() {
        return new ImgSettingScreen();
    }

    @Override
    public void addindex(Player player) {
        ImgSetting.addindex(player);
    }

    @Override
    public File getImg() {
        return ImgSetting.getImg();
    }
}
