package com.ywsuoyi.loader.beaconLoader;

import com.ywsuoyi.colorspace.ColorSpaces;
import com.ywsuoyi.loader.AbstractLoader;
import com.ywsuoyi.loadingThreadUtil.BaseThread;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class BeaconLoader extends AbstractLoader {
    public BeaconLoader(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide && context.getPlayer() != null) {
            if (!context.getPlayer().isShiftKeyDown()) {
                addindex(context.getPlayer());
            } else if (getImg() == null) {
                context.getPlayer().displayClientMessage(Component.translatable("pixelLoader.noFile"), true);
            } else {
                try {
                    BaseThread thread = getThread(context);
                    BaseThread.addThread(thread);
                } catch (Exception e) {
                    context.getPlayer().displayClientMessage(Component.translatable("pixelLoader.LoadingThread.error", e.getMessage()), true);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BaseThread getThread(UseOnContext context) {
        return new LoadBeaconThread(
                context.getPlayer(),
                BeaconSetting.getImg(),
                BeaconSetting.dither,
                context.getLevel(),
                context.getClickedPos().offset(context.getClickedFace().getNormal()),
                BeaconSetting.size);
    }

    @Override
    public Screen getScreen() {
        return new BeaconSettingScreen();
    }

    @Override
    public void addindex(Player player) {
        BeaconSetting.addindex(player);
    }

    @Override
    public File getImg() {
        return BeaconSetting.getImg();
    }
}
