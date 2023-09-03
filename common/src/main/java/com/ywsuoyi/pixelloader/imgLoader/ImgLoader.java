package com.ywsuoyi.pixelloader.imgLoader;

import com.ywsuoyi.pixelloader.Setting;
import com.ywsuoyi.pixelloader.colorspace.ColorSpace;
import com.ywsuoyi.pixelloader.loadingThreadUtil.BaseThread;
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
        if (!ColorSpace.allLoad()) {
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
                BaseThread.addThread(new LoadImgThread(
                        context.getPlayer(),
                        Setting.getImg(),
                        Setting.fs,
                        Setting.imgSize,
                        Setting.cutout,
                        context.getLevel(),
                        context.getClickedPos(),
                        context.getClickedPos().offset(context.getClickedFace().getNormal()),
                        Setting.pm
                ));
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
