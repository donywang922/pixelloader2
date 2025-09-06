package com.ywsuoyi.loader;

import com.ywsuoyi.Setting;
import com.ywsuoyi.colorspace.ColorSpaces;
import com.ywsuoyi.loadingThreadUtil.BaseThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractLoader extends Item {
    public AbstractLoader(Properties properties) {
        super(properties);
    }

    public abstract BaseThread getThread(UseOnContext context);

    public abstract Screen getScreen();

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (!ColorSpaces.allLoad()) {
            if (context.getPlayer() != null)
                context.getPlayer().displayClientMessage(Component.translatable("pixelLoader.colored_block.needload"), true);
            return InteractionResult.FAIL;
        }
        if (!context.getLevel().isClientSide && context.getPlayer() != null) {
            if (!context.getPlayer().isShiftKeyDown()) {
                Setting.addindex(context.getPlayer());
            } else if (Setting.imglist.isEmpty()) {
                context.getPlayer().displayClientMessage(Component.translatable("pixelLoader.noFile"), true);
            } else {
                BaseThread.addThread(getThread(context));
            }
        }
        return InteractionResult.SUCCESS;
    }


    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (playerIn.isShiftKeyDown()) {
            if (!worldIn.isClientSide) Setting.addindex(playerIn);
        } else {
            if (worldIn.isClientSide)
                Minecraft.getInstance().setScreen(getScreen());
        }
        return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("pixelLoader.loader.tip1"));
        list.add(Component.translatable("pixelLoader.loader.tip2"));
        list.add(Component.translatable("pixelLoader.loader.tip3"));
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
    }
}
