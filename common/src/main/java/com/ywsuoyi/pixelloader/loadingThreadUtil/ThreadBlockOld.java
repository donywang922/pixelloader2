package com.ywsuoyi.pixelloader.loadingThreadUtil;

import com.ywsuoyi.pixelloader.Setting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThreadBlockOld extends Block {
    public static final IntegerProperty threadNO = IntegerProperty.create("threadno", 0, 16);

    public ThreadBlockOld(Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.registerDefaultState(this.stateDefinition.any().setValue(threadNO, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> p_49915_) {
        p_49915_.add(threadNO);
        super.createBlockStateDefinition(p_49915_);
    }

    @Override
    public float getShadeBrightness(BlockState arg, BlockGetter arg2, BlockPos arg3) {
        return 1.0f;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState arg, BlockGetter arg2, BlockPos arg3) {
        return true;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
        Setting.stopThread(blockState.getValue(threadNO));
        super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack);
    }


    @Override
    public @NotNull InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (Setting.threads.containsKey(state.getValue(threadNO)))
            player.displayClientMessage(Setting.threads.get(state.getValue(threadNO)).message, true);
        return InteractionResult.SUCCESS;
    }


    @Override
    public void stepOn(Level world, BlockPos pos, @NotNull BlockState blockState, Entity entity) {
        if (Setting.threads.containsKey(world.getBlockState(pos).getValue(threadNO)))
            if (entity instanceof Player)
                ((Player) entity).displayClientMessage(
                        Setting.threads.get(world.getBlockState(pos).getValue(threadNO)).message, true);
        super.stepOn(world, pos, blockState, entity);
    }

}
