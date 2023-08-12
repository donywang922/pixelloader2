package com.ywsuoyi.pixelloader.loadingThreadUtil;

import com.ywsuoyi.pixelloader.PixelLoader;
import com.ywsuoyi.pixelloader.Setting;
import com.ywsuoyi.pixelloader.colorspace.ColorSettingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThreadBlock extends BaseEntityBlock {
    public ThreadBlock(Properties properties) {
        super(properties);
    }

    @Override
    public float getShadeBrightness(BlockState arg, BlockGetter arg2, BlockPos arg3) {
        return 1.0f;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState arg, BlockGetter arg2, BlockPos arg3) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ThreadBlockEntity(blockPos, blockState);
    }

    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide)
            Minecraft.getInstance().setScreen(new ThreadScreen(blockPos));
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide)
            return createTickerHelper(blockEntityType, PixelLoader.threadBlockEntity, ThreadBlockEntity::renderTick);
        return createTickerHelper(blockEntityType, PixelLoader.threadBlockEntity, ThreadBlockEntity::serverTick);
    }

    @Override
    public void stepOn(Level world, BlockPos pos, @NotNull BlockState blockState, Entity entity) {
        ThreadData data = ThreadData.getData(pos);
        if (data != null && entity instanceof Player)
            ((Player) entity).displayClientMessage(data.thread.message, true);
        super.stepOn(world, pos, blockState, entity);
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        ThreadData data = ThreadData.getData(blockPos);
        if (data != null)
            data.thread.forceStop();
        ThreadData.data.remove(blockPos);
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }
}
