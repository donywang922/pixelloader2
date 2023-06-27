package com.ywsuoyi.pixelloader;

import net.minecraft.client.Minecraft;
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

public class ImgLoader extends Item {


    public ImgLoader(Properties settings) {
        super(settings);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (!Setting.ed) {
            if (context.getPlayer() != null)
                context.getPlayer().displayClientMessage(Component.translatable("pixelLoader.colored_block.needload"), true);
            return InteractionResult.FAIL;
        }
        if (!context.getLevel().isClientSide && context.getPlayer() != null) {
            if (!context.getPlayer().isShiftKeyDown()) {
                Setting.addindex(context.getPlayer());
            } else if (Setting.imglist.size() == 0) {
                context.getPlayer().displayClientMessage(Component.translatable("pixelLoader.noFile"), true);
            } else {
                for (int i = 1; i <= 16; i++) {
                    if (!Setting.threads.containsKey(i)) {
                        LoadingThread thread = new LoadImgThread(Setting.getImg(), context, Setting.imgSize, Setting.pm, i, Setting.fs);
                        Setting.threads.put(i, thread);
                        Setting.startNextThread();
                        return InteractionResult.SUCCESS;
                    }
                }
                context.getPlayer().displayClientMessage(Component.translatable("pixelLoader.LoadingThread.fill"), true);
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
                Minecraft.getInstance().setScreen(new ImgSettingScreen());
        }
        return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("pixelLoader.imgLoader.tip"));
        super.appendHoverText(stack, world, tooltip, context);
    }
}
