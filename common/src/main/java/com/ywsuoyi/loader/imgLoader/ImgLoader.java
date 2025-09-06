package com.ywsuoyi.loader.imgLoader;

import com.ywsuoyi.Setting;
import com.ywsuoyi.loader.AbstractLoader;
import com.ywsuoyi.loadingThreadUtil.BaseThread;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.context.UseOnContext;

public class ImgLoader extends AbstractLoader {


    public ImgLoader(Properties properties) {
        super(properties);
    }

    @Override
    public BaseThread getThread(UseOnContext context) {
        return new LoadImgThread(
                context.getPlayer(),
                Setting.getImg(),
                Setting.dither,
                Setting.imgSize,
                Setting.cutout,
                context.getLevel(),
                context.getClickedPos(),
                context.getClickedPos().offset(context.getClickedFace().getNormal()),
                Setting.flat
        );
    }

    @Override
    public Screen getScreen() {
        return new ImgSettingScreen();
    }
}
