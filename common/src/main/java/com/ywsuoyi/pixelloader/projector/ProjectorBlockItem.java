package com.ywsuoyi.pixelloader.projector;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ProjectorBlockItem extends BlockItem {
    public ProjectorBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        BlockPos pos = player.blockPosition().above();
        level.setBlock(pos, getBlock().defaultBlockState(), 11);
        BlockState state = level.getBlockState(pos);
        state.getBlock().setPlacedBy(level, pos, state, player, player.getItemInHand(interactionHand));
        return InteractionResultHolder.success(player.getItemInHand(interactionHand));
    }
}
