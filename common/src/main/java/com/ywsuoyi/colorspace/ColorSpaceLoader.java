package com.ywsuoyi.colorspace;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ColorSpaceLoader extends Item {
    public ColorSpaceLoader(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        super.inventoryTick(itemStack, level, entity, i, bl);
        if (!level.isClientSide) {
            if (ColorSpaces.waitPlace) {
                ColorSpaces.waitPlace = false;
                BlockPos pos = entity.blockPosition();
                if (ColorSpaces.allLoad()) {
                    ColorSpaces.blockSpace.blocks.forEach(coloredBlock -> {
                        level.setBlock(pos.offset(coloredBlock.r / 6, coloredBlock.g / 6 - 1, coloredBlock.b / 6), Blocks.GLASS.defaultBlockState(), 3);
                        level.setBlock(pos.offset(coloredBlock.r / 6, coloredBlock.g / 6, coloredBlock.b / 6), coloredBlock.block.defaultBlockState(), 3);
                    });
                }
            }
            if (ColorSpaces.openFilter && entity instanceof Player) {
                ColorSpaces.openFilter = false;
                ((Player) entity).openMenu(new SimpleMenuProvider((id, inventory, playerIn) ->
                        ChestMenu.sixRows(id, inventory, new FilterInv()),
                        Component.translatable("pixelLoader.colorspace.screen.filter")));
            }
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand handIn) {
        if (worldIn.isClientSide)
            Minecraft.getInstance().setScreen(new ColorSettingScreen(player));
        return InteractionResultHolder.success(player.getItemInHand(handIn));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("pixelLoader.colorspace.tip"));
        list.add(Component.translatable("pixelLoader.colorspace.screen.message",
                ColorSpaces.thread != null ? ColorSpaces.thread.message : Component.empty()));
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
    }
}
