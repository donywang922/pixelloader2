package com.ywsuoyi.pixelloader.colorspace;

import com.ywsuoyi.pixelloader.Setting;
import com.ywsuoyi.pixelloader.banBlockInv;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ColorSpaceLoader extends Item {
    public ColorSpaceLoader(Properties properties) {
        super(properties);
    }

    public void load(Level world, Player player) {
        ColorSpace.filter = Setting.banItem;
        ColorSpace.thread = new LoadColorSpaceThread(player, world);
        ColorSpace.thread.start();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand handIn) {
        if (!worldIn.isClientSide) if (player.isShiftKeyDown()) {
            load(worldIn, player);
        } else {
            player.openMenu(new SimpleMenuProvider((id, inventory, playerIn) -> ChestMenu.sixRows(id, inventory, new banBlockInv()), Setting.banItemScreen));
        }
        return InteractionResultHolder.success(player.getItemInHand(handIn));
    }


    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            if (context.getPlayer() != null) if (context.getPlayer().isShiftKeyDown()) {
                if (ColorSpace.allLoad()) {
                    ColorSpace.blockSpace.blocks.forEach(coloredBlock -> {
                        context.getLevel().setBlock(context.getClickedPos().offset(coloredBlock.r / 6, coloredBlock.g / 6 - 1, coloredBlock.b / 6), Blocks.GLASS.defaultBlockState(), 3);
                        context.getLevel().setBlock(context.getClickedPos().offset(coloredBlock.r / 6, coloredBlock.g / 6, coloredBlock.b / 6), coloredBlock.block.defaultBlockState(), 3);
                    });
                } else load(context.getLevel(), context.getPlayer());
            } else {
                context.getPlayer().openMenu(new SimpleMenuProvider((id, inventory, playerIn) -> ChestMenu.sixRows(id, inventory, new banBlockInv()), Setting.banItemScreen));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("pixelLoader.colored_block.tip"));
        if (ColorSpace.allLoad()) {
            tooltip.add(Component.translatable("pixelLoader.colored_block.place"));
            tooltip.add(Component.translatable("pixelLoader.colored_block.reload"));
        } else {
            tooltip.add(Component.translatable("pixelLoader.colored_block.load"));
        }
        super.appendHoverText(stack, world, tooltip, context);
    }
}
